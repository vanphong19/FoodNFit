package com.vanphong.foodnfit.admin.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivityExerciseInfoBinding
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.ExerciseInfoViewModel

class ExerciseInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseInfoBinding
    private val viewModel: ExerciseInfoViewModel by viewModels()
    private var exerciseId: Int? = null
    private var loadingDialog: AlertDialog? = null
    private val editExerciseLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            exerciseId?.let {
                viewModel.loadExerciseInfo(it)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.infoViewModel = viewModel
        exerciseId = intent.getIntExtra("exercise_id", 0)
        exerciseId?.let {
            viewModel.loadExerciseInfo(it)
        }

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(this, getString(R.string.loading))
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }
        setUI()
    }

    private fun setUI(){
        binding.btnUpdateExercise.setOnClickListener {
            val intent = Intent(this, AddEditExerciseActivity::class.java)
            intent.putExtra("exercise_id", exerciseId)
            intent.putExtra("title", getString(R.string.update_exercise))
            editExerciseLauncher.launch(intent)
        }
        binding.btnDeleteExercise.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.delete_exercise_messssage))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    exerciseId?.let { it1 -> viewModel.remove(it1) }
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
        viewModel.notify.observe(this) { message ->
            when {
                message == "removed" -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                message.startsWith("error:") -> {
                    Toast.makeText(this, "Xóa thất bại: ${message.removePrefix("error:")}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}