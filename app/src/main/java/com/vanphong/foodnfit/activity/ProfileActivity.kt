package com.vanphong.foodnfit.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivityProfileBinding
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.util.ImageUtils
import com.vanphong.foodnfit.viewModel.ProfileViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var hasImageChanged = false // Track if image has been changed
    private var shouldSave = false
    private var dialogImgAvatar: CircleImageView? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            hasImageChanged = true // Mark image as changed

            dialogImgAvatar?.setImageURI(selectedImageUri)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        setupClickListeners()
        setupObservers()
        viewModel.loadInfo()
    }

    private fun setupClickListeners() {
        // Edit Goal Info
        binding.imgEdit2.setOnClickListener {
            showEditGoalDialog()
        }

        // Edit Personal Info
        binding.imgEdit1.setOnClickListener {
            showEditPersonalDialog()
        }
    }

    private fun setUI(){
        viewModel.avtUrl.observe(this){
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.male)
                .error(R.drawable.male)
                .into(binding.cimgAvt)
        }

        viewModel.gender.observe(this){gender ->
            if(gender){
                binding.textGender.text = getString(R.string.female)
            }
            else{
                binding.textGender.text = getString(R.string.male)
            }
        }

        viewModel.loading.observe(this){loading ->
            if(loading){
                DialogUtils.showLoadingDialog(this, getString(R.string.loading))
            }
            else{
                DialogUtils.hideLoadingDialog()
            }
        }
    }

    private fun showEditGoalDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_goal_info, null)

        // Get references to dialog views
        val spinnerFoodGoal = dialogView.findViewById<Spinner>(R.id.spinner_food_goal)
        val spinnerExerciseGoal = dialogView.findViewById<Spinner>(R.id.spinner_exercise_goal)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btn_cancel_goal)
        val btnSave = dialogView.findViewById<MaterialButton>(R.id.btn_save_goal)

        // Setup Food Goal Spinner
        val mealGoalAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.meal_goal,
            android.R.layout.simple_spinner_item
        )
        mealGoalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFoodGoal.adapter = mealGoalAdapter

        // Setup Exercise Goal Spinner
        val exerciseGoalAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.activity_levels,
            android.R.layout.simple_spinner_item
        )
        exerciseGoalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExerciseGoal.adapter = exerciseGoalAdapter

        val currentMealGoal = viewModel.mealGoal.value ?: ""
        val currentExerciseGoal = viewModel.exerciseGoal.value ?: ""

        // Find and set current meal goal position
        val mealGoalArray = resources.getStringArray(R.array.meal_goal)
        val currentMealPosition = mealGoalArray.indexOf(currentMealGoal)
        if (currentMealPosition >= 0) {
            spinnerFoodGoal.setSelection(currentMealPosition)
        }

        // Find and set current exercise goal position
        val exerciseGoalArray = resources.getStringArray(R.array.activity_levels)
        val currentExercisePosition = exerciseGoalArray.indexOf(currentExerciseGoal)
        if (currentExercisePosition >= 0) {
            spinnerExerciseGoal.setSelection(currentExercisePosition)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Handle button clicks
        btnCancel.setOnClickListener {
            viewModel.loadInfo()
            dialog.dismiss()
        }

        dialogView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.performClick() // 👈 Gọi để tránh warning
                val currentFocus = dialog.currentFocus ?: v
                v.context.hideKeyboard(currentFocus)
                v.clearFocus()
            }
            false
        }

        btnSave.setOnClickListener {
            val selectedFoodGoal = spinnerFoodGoal.selectedItem.toString()
            val selectedExerciseGoal = spinnerExerciseGoal.selectedItem.toString()

            // Validation (Optional - spinners always have a selection)
            if (selectedFoodGoal.isEmpty() || selectedExerciseGoal.isEmpty()) {
                // This shouldn't happen with spinners, but just in case
                Toast.makeText(this, "Please select both goals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateGoal(selectedFoodGoal, selectedExerciseGoal)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditPersonalDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_personal_info, null)

        // Get references to dialog views
        val etName = dialogView.findViewById<TextInputEditText>(R.id.et_name)
        val etAge = dialogView.findViewById<TextInputEditText>(R.id.et_age)
        val etHeight = dialogView.findViewById<TextInputEditText>(R.id.et_height)
        val etWeight = dialogView.findViewById<TextInputEditText>(R.id.et_weight)
        val rgGender = dialogView.findViewById<RadioGroup>(R.id.rg_gender)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btn_cancel_personal)
        val btnSave = dialogView.findViewById<MaterialButton>(R.id.btn_save_personal)
        val imgAvatar = dialogView.findViewById<CircleImageView>(R.id.imgAvatar)
        val btnChangeAvatar = dialogView.findViewById<Button>(R.id.btnChangeAvatar)
        dialogImgAvatar = imgAvatar

        // Reset flags
        hasImageChanged = false
        selectedImageUri = null

        // Set current values
        etName.setText(viewModel.name.value ?: "")
        etAge.setText(viewModel.birthday.value ?: "")
        etHeight.setText(viewModel.height.value ?: "")
        etWeight.setText(viewModel.weight.value ?: "")

        // Set current gender selection
        val currentGender = viewModel.gender.value ?: false
        when (currentGender) {
            false -> rgGender.check(R.id.rb_male)
            true -> rgGender.check(R.id.rb_female)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Load current avatar
        viewModel.avtUrl.value?.let { url ->
            Glide.with(this).load(url).placeholder(R.drawable.male).error(R.drawable.male).into(imgAvatar)
        }

        // Handle button clicks
        btnCancel.setOnClickListener {
            dialog.dismiss()
            selectedImageUri = null
            hasImageChanged = false
            dialogImgAvatar = null
        }

        dialogView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.performClick()
                val currentFocus = dialog.currentFocus ?: v
                v.context.hideKeyboard(currentFocus)
                v.clearFocus()
            }
            false
        }

        btnChangeAvatar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        etAge.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                etAge.setText(selectedDate)
            }, year, month, day).show()
        }

        btnSave.setOnClickListener {
            val fullname = etName.text.toString().trim()
            val birthday = etAge.text.toString().trim()
            val newHeight = etHeight.text.toString().trim()
            val newWeight = etWeight.text.toString().trim()
            val selectedGenderId = rgGender.checkedRadioButtonId
            val newGender = when (selectedGenderId) {
                R.id.rb_male -> false
                R.id.rb_female -> true
                else -> false
            }

            // Validation
            if (fullname.isEmpty()) {
                etName.error = "Name cannot be empty"
                return@setOnClickListener
            }

            if (birthday.isEmpty()) {
                etAge.error = "Birthday cannot be empty"
                return@setOnClickListener
            }

            if (newHeight.isEmpty()) {
                etHeight.error = "Height cannot be empty"
                return@setOnClickListener
            }

            if (newWeight.isEmpty()) {
                etWeight.error = "Weight cannot be empty"
                return@setOnClickListener
            }

            // Additional validation for numeric values
            try {
                val height = newHeight.toDouble()
                val weight = newWeight.toDouble()

                if (height <= 0 || height > 300) {
                    etHeight.error = "Please enter a valid height (1-300 cm)"
                    return@setOnClickListener
                }

                if (weight <= 0 || weight > 500) {
                    etWeight.error = "Please enter a valid weight (1-500 kg)"
                    return@setOnClickListener
                }

                // Kiểm tra xem có ảnh mới được chọn không
                if (hasImageChanged && selectedImageUri != null) {
                    // Upload ảnh trước, sau đó update info
                    uploadImageThenUpdateInfo(fullname, newGender, birthday, newHeight, newWeight)
                } else {
                    // Không có ảnh mới, chỉ update thông tin
                    viewModel.updateInfo(fullname, newGender, birthday, newHeight, newWeight)
                }

                dialog.dismiss()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun uploadImageThenUpdateInfo(
        fullname: String,
        gender: Boolean,
        birthday: String,
        height: String,
        weight: String
    ) {
        if (selectedImageUri == null) {
            // Fallback: update info without image
            viewModel.updateInfo(fullname, gender, birthday, height, weight)
            return
        }

        val resizedFile = ImageUtils.resizeImage(selectedImageUri!!, this)
        if (resizedFile == null) {
            Toast.makeText(this, "Không thể xử lý ảnh", Toast.LENGTH_SHORT).show()
            // Fallback: update info without image
            viewModel.updateInfo(fullname, gender, birthday, height, weight)
            return
        }

        Log.d("FileCheck", "File exists: ${resizedFile.exists()}, size: ${resizedFile.length()}")

        // Sử dụng ViewModel để upload image
        viewModel.uploadImage(resizedFile)

        // Observe avatar URL change và update info sau khi upload xong
        viewModel.avtUrl.observe(this) { url ->
            if (url != null && hasImageChanged) {
                // Reset flag để tránh gọi lại nhiều lần
                hasImageChanged = false
                // Update thông tin sau khi upload ảnh xong
                viewModel.updateInfo(fullname, gender, birthday, height, weight)
            }
        }
    }

    // Thêm method để setup observers trong onCreate
    private fun setupObservers() {
        viewModel.avtUrl.observe(this) {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.male)
                .error(R.drawable.male)
                .into(binding.cimgAvt)
        }

        viewModel.gender.observe(this) { gender ->
            if (gender) {
                binding.textGender.text = getString(R.string.female)
            } else {
                binding.textGender.text = getString(R.string.male)
            }
        }

        viewModel.loading.observe(this) { loading ->
            if (loading) {
                DialogUtils.showLoadingDialog(this, getString(R.string.loading))
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun Context.hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}