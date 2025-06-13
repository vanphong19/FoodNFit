package com.vanphong.foodnfit.admin.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.vanphong.foodnfit.Model.ExerciseRequest
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.viewModel.ExerciseViewModel
import com.vanphong.foodnfit.databinding.ActivityAddEditExerciseBinding
import com.vanphong.foodnfit.util.FileUtils

class AddEditExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditExerciseBinding
    private val viewModel: ExerciseViewModel by viewModels()

    private var selectedImageUri: Uri? = null

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

        // Quan sát url ảnh upload thành công
        viewModel.imageUrl.observe(this) { url ->
            if (!url.isNullOrEmpty()) {
                viewModel.createExercise()
            }
        }

        // Quan sát kết quả tạo bài tập
        viewModel.exerciseCreated.observe(this) { exercise ->
            if (exercise != null) {
                Toast.makeText(this, "Tạo bài tập thành công: ${exercise.name}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Quan sát lỗi
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabSaveExercise.setOnClickListener {
            uploadImageAndCreateExercise()
        }
    }
    private fun uploadImageAndCreateExercise() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
            return
        }
        val file = FileUtils.getFileFromUri(this, selectedImageUri!!)
        if (file == null) {
            Toast.makeText(this, "Không lấy được file ảnh", Toast.LENGTH_SHORT).show()
            return
        }
        // Gọi upload ảnh, khi upload xong sẽ có url trigger observer để tạo bài tập
        viewModel.uploadImage(file)
    }
}