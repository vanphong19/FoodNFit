package com.vanphong.foodnfit.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.Reminder
import com.vanphong.foodnfit.adapter.NotificationAdapter
import com.vanphong.foodnfit.databinding.ActivityNotificationBinding
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.ReminderViewModel
import java.time.LocalDateTime
import java.util.UUID

class NotificationActivity : BaseActivity() {
    private var _binding: ActivityNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReminderViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.reminderViewModel = viewModel
        iniNotification()
        viewModel.getAllReminder()
        viewModel.isLoading.observe(this){
            if (it){
                DialogUtils.showLoadingDialog(this, getString(R.string.loading))
            }
            else{
                DialogUtils.hideLoadingDialog()
            }
        }
    }

    private fun iniNotification(){
        notificationAdapter = NotificationAdapter(
            onItemClick = {
                val intent = Intent(this, ReminderInfoActivity::class.java)
                intent.putExtra("reminder_id", it)
                startActivity(intent)
            }
        )
        binding.rvNotification.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.reminderList.observe(this){list ->
            notificationAdapter.submitList(list)
        }
        viewModel.reminderList.observe(this) { list ->
            notificationAdapter.submitList(list)
        }

        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvNotification.adapter = notificationAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}