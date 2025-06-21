package com.vanphong.foodnfit.admin.activity

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.viewModel.FoodDetailViewModel
import com.vanphong.foodnfit.databinding.ActivityAddEditFoodBinding
import com.vanphong.foodnfit.repository.FoodTypeRepository
import com.vanphong.foodnfit.util.FileUtils
import com.vanphong.foodnfit.util.ImageUtils
import kotlinx.coroutines.launch

class AddEditFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditFoodBinding
    private var foodId: Int? = null
    private var title: String? = null
    private val viewModel: FoodDetailViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var shouldSave = false
    private var originalImageUrl: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivFoodImagePreview.load(it) // Hiển thị ảnh ngay khi chọn
            // Reset imageUrl trong ViewModel vì ảnh mới được chọn, chưa upload
            viewModel.setImageUrl(null)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.addEditViewModel = viewModel
        foodId = intent.getIntExtra("food_id", 0)
        title = intent.getStringExtra("title")
        binding.tvTitle.text = title
        viewModel.setFoodTypes()
        setFoodType()

        binding.fabSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        setUI()

        viewModel.imageUrl.observe(this) { url ->
            if (!url.isNullOrEmpty() && shouldSave) {
                shouldSave = false
                if(title == getString(R.string.edit_food)){
                    viewModel.updateFood(foodId!!)
                }
                else{
                    viewModel.createFood()
                }
            }
        }

        viewModel.createdFood.observe(this) { food ->
            if (food != null) {
                if(title == getString(R.string.add_food)) {
                    Toast.makeText(
                        this,
                        getString(R.string.create_food_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Toast.makeText(
                        this,
                        getString(R.string.update_food_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                shouldSave = false
                finish()
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                shouldSave = false
            }
        }

        binding.btnSave.setOnClickListener {
            shouldSave = true
            val isEdit = title == getString(R.string.edit_food)

            if (selectedImageUri == null) {
                if (isEdit) {
                    viewModel.setImageUrl(originalImageUrl)
                } else {
                    Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
                }
            } else{
                uploadImageAndCreateFood()
            }
         }
    }

    private fun setUI(){
        if(title == getString(R.string.edit_food) && foodId != null && foodId != 0){
            viewModel.getById(foodId!!)
            binding.btnSave.text = getString(R.string.edit_food)
            viewModel.imageUrl.observe(this){ url ->
                originalImageUrl = url
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(binding.ivFoodImagePreview)
            }
            viewModel.selectedFoodTypeId.observe(this){id->
                val foodTypeList = viewModel.foodTypes.value
                if (foodTypeList != null) {
                    val selectedFoodType = foodTypeList.find { it.id == id }
                    binding.actvFoodType.setText(selectedFoodType?.name ?: "", false)
                }
            }
        }
        else {
            viewModel.setFoodDetail()
            binding.btnSave.text = getString(R.string.save_food)
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
    private fun uploadImageAndCreateFood() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        val resizedFile = ImageUtils.resizeImage(selectedImageUri!!, this)
        if (resizedFile == null) {
            Toast.makeText(this, "Không thể xử lý ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("FileCheck", "File exists: ${resizedFile.exists()}, size: ${resizedFile.length()}")
        viewModel.uploadImage(resizedFile)
    }

    private fun setFoodType() {
        viewModel.foodTypes.observe(this){foodTypeList ->
            val names = foodTypeList.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, names)
            binding.actvFoodType.setAdapter(adapter)

            // Hiển thị tên nếu đã có ID chọn sẵn
            viewModel.selectedFoodTypeId.value?.let { selectedId ->
                val selected = foodTypeList.find { it.id == selectedId }
                binding.actvFoodType.setText(selected?.name ?: "", false)
            }

            // Xử lý khi chọn item
            binding.actvFoodType.setOnItemClickListener { _, _, position, _ ->
                val selectedId = foodTypeList[position].id
                viewModel.setSelectedFoodTypeId(selectedId)
                Log.d("id", selectedId.toString())
            }
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}