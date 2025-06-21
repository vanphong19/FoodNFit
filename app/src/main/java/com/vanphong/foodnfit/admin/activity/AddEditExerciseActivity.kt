package com.vanphong.foodnfit.admin.activity

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.viewModel.ExerciseViewModel
import com.vanphong.foodnfit.databinding.ActivityAddEditExerciseBinding
import com.vanphong.foodnfit.util.FileUtils
import com.vanphong.foodnfit.util.ImageUtils

class AddEditExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditExerciseBinding
    private val viewModel: ExerciseViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    private var exerciseId: Int? = null
    private var title: String? = null
    private var shouldSave = false
    private var originalImageUrl: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.exerciseImage.load(it) // Hiển thị ảnh ngay khi chọn
            // Reset imageUrl trong ViewModel vì ảnh mới được chọn, chưa upload
            viewModel.setImageUri(null)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.exerciseViewModel = viewModel

        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        title = intent.getStringExtra("title")
        exerciseId = intent.getIntExtra("exercise_id", 1)
        binding.toolbar.title = title

        // Quan sát url ảnh upload thành công
        viewModel.imageUrl.observe(this) { url ->
            if (!url.isNullOrEmpty() && shouldSave) {
                shouldSave = false
                if(title == getString(R.string.update_exercise)){
                    viewModel.updateExercise(exerciseId!!)
                }
                else{
                    viewModel.createExercise()
                }
            }
        }

        // Quan sát kết quả tạo bài tập
        viewModel.exerciseCreated.observe(this) { exercise ->
            if (exercise != null) {
                if(title == getString(R.string.add_exercise)) {
                    Toast.makeText(
                        this,
                        getString(R.string.create_exercise_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Toast.makeText(
                        this,
                        getString(R.string.update_exercise_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                shouldSave = false
                setResult(RESULT_OK)
                finish()
            }
        }

        // Quan sát lỗi
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                shouldSave = false
            }
        }

        binding.btnSave.setOnClickListener {
            shouldSave = true
            val isEdit = title == getString(R.string.update_exercise)

            if (selectedImageUri == null) {
                if (isEdit) {
                    viewModel.setImageUri(originalImageUrl)
                } else {
                    Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
                }
            } else{
                uploadImageAndCreateExercise()
            }
        }

        setUI()
    }
    private fun setUI(){
        if(title == getString(R.string.update_exercise) && exerciseId != null && exerciseId != 0){
            viewModel.loadExerciseInfo(exerciseId!!)
            binding.btnSave.text = getString(R.string.update_exercise)
            viewModel.imageUrl.observe(this){ url ->
                originalImageUrl = url
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(binding.exerciseImage)
            }
        }
        else {
            binding.btnSave.text = getString(R.string.save_exercise)
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
        val categories = resources.getStringArray(R.array.exercise_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvExerciseCategory.setAdapter(adapter)
    }
    private fun uploadImageAndCreateExercise() {
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