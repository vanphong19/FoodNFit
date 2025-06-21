package com.vanphong.foodnfit.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivityReminderInfoBinding
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.ReminderViewModel
import kotlin.properties.Delegates

class ReminderInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderInfoBinding
    private val viewModel: ReminderViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.reminderViewModel = viewModel
        val reminderId = intent.getIntExtra("reminder_id", 0)

        viewModel.getById(reminderId)

        viewModel.isLoading.observe(this){
            if(it){
                DialogUtils.showLoadingDialog(this, getString(R.string.loading))
            }
            else{
                DialogUtils.hideLoadingDialog()
            }
        }

        viewModel.error.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}