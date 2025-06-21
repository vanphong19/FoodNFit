package com.vanphong.foodnfit.admin.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.viewModel.AddEditAccountViewModel
import com.vanphong.foodnfit.databinding.ActivityAddEditAccountBinding
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.util.FileUtils
import com.vanphong.foodnfit.util.ImageUtils
import java.util.Calendar

class AddEditAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditAccountBinding
    private val viewModel: AddEditAccountViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var userId: String? = null
    private var isEditMode = false
    private var originalImageUrl: String? = null
    private var hasImageChanged = false // Track if image has been changed
    private var shouldSave = false

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            hasImageChanged = true // Mark image as changed
            binding.imgAvatar.setImageURI(it)
            // Don't set avatar URL here, we'll handle it in the save process
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.addEditViewModel = viewModel

        // Check if it's edit mode
        userId = intent.getStringExtra("user_id")
        isEditMode = userId != null

        setupUI()
        setupListeners()
        observeViewModel()

        if (isEditMode) {
            binding.tvTitle.text = getString(R.string.update_account)
            userId?.let { viewModel.loadUserData(it) }
            // Hide password field in edit mode
            binding.layoutPassword.visibility = android.view.View.GONE
        } else {
            binding.tvTitle.text = getString(R.string.add_account)
            // Show password field in create mode
            binding.layoutPassword.visibility = android.view.View.VISIBLE
            // Set default avatar for create mode
            binding.imgAvatar.setImageResource(R.drawable.male)
        }
    }

    private fun setupUI() {
        // Setup gender radio buttons
        binding.radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioMale -> viewModel.setGender(false)
                R.id.radioFemale -> viewModel.setGender(true)
            }
        }

        // Setup birthday picker
        binding.edtBirthday.setOnClickListener {
            showDatePicker()
        }

        binding.btnChangeAvatar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        viewModel.avatar.observe(this){ url ->
            if (!url.isNullOrEmpty() && shouldSave) {
                shouldSave = false
                if(isEditMode){
                    viewModel.updateUser(userId!!)
                }
                else{
                    viewModel.createUser()
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.edtBirthday.setText(selectedDate)
            viewModel.setBirthday(selectedDate)
        }, year, month, day).show()
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            shouldSave = true
            if (selectedImageUri == null) {
                if (isEditMode) {
                    shouldSave = true
                    viewModel.setAvatar(originalImageUrl ?: "")
                } else {
                    Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                uploadImageAndCreateExercise()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(this, getString(R.string.processing))
            } else {
                DialogUtils.hideLoadingDialog()
            }
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                val message = if (isEditMode) {
                    getString(R.string.update_account_success)
                } else {
                    getString(R.string.create_account_success)
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            result.onFailure { error ->
                val message = if (isEditMode) {
                    getString(R.string.update_account_failed)
                } else {
                    getString(R.string.create_account_failed)
                }
                Toast.makeText(this, "$message: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Observe user data for edit mode
        viewModel.fullname.observe(this) { fullname ->
            if (fullname != null && binding.edtFullname.text.toString() != fullname) {
                binding.edtFullname.setText(fullname)
            }
        }

        viewModel.email.observe(this) { email ->
            if (email != null && binding.edtEmail.text.toString() != email) {
                binding.edtEmail.setText(email)
            }
        }

        viewModel.birthday.observe(this) { birthday ->
            if (birthday != null && binding.edtBirthday.text.toString() != birthday) {
                binding.edtBirthday.setText(birthday)
            }
        }

        viewModel.gender.observe(this) { gender ->
            when (gender) {
                true -> binding.radioFemale.isChecked = true
                false -> binding.radioMale.isChecked = true
                null -> {
                    binding.radioGroupGender.clearCheck()
                }
            }
        }

        viewModel.avatar.observe(this) { avatarUrl ->
            if (!avatarUrl.isNullOrEmpty() && !hasImageChanged) {
                originalImageUrl = avatarUrl // Store original image URL
                Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.male)
                    .error(R.drawable.male)
                    .into(binding.imgAvatar)
            }
        }
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