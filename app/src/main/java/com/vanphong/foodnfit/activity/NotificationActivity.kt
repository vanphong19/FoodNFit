package com.vanphong.foodnfit.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.Model.Reminder
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.NotificationAdapter
import com.vanphong.foodnfit.databinding.ActivityNotificationBinding
import com.vanphong.foodnfit.viewModel.ReminderViewModel
import java.time.LocalDateTime
import java.util.UUID

class NotificationActivity : AppCompatActivity() {
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
    }

    private fun iniNotification(){
        notificationAdapter = NotificationAdapter()
        binding.rvNotification.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.reminderList.observe(this){list ->
            notificationAdapter.submitList(list)
        }
        viewModel.setReminderList(getData())
        binding.rvNotification.adapter = notificationAdapter
    }

    private fun getData(): List<Reminder> {
        return listOf(
            Reminder(
                id = 1,
                userId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
                reminderType = "Workout",
                message = "Don't forget your workout today!",
                scheduledTime = LocalDateTime.of(2025, 5, 13, 7, 0),
                isActive = true,
                isRead = false
            ),
            Reminder(
                id = 2,
                userId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
                reminderType = "Meal",
                message = "Time for your lunch!",
                scheduledTime = LocalDateTime.of(2025, 5, 14, 12, 30),
                isActive = true,
                isRead = true
            ),
            Reminder(
                id = 3,
                userId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
                reminderType = "Water",
                message = "Drink a glass of water!",
                scheduledTime = LocalDateTime.of(2025, 5, 14, 10, 0),
                isActive = false,
                isRead = false
            ),
            Reminder(
                id = 4,
                userId = UUID.fromString("b1234567-1111-2222-3333-abcdefabcdef"),
                reminderType = "Weigh-in",
                message = "Time to check your weight.",
                scheduledTime = LocalDateTime.of(2025, 5, 15, 8, 0),
                isActive = true,
                isRead = false
            ),
            Reminder(
                id = 5,
                userId = UUID.fromString("b1234567-1111-2222-3333-abcdefabcdef"),
                reminderType = "Sleep",
                message = "Get ready for bed by 10 PM.",
                scheduledTime = LocalDateTime.of(2025, 5, 14, 21, 30),
                isActive = true,
                isRead = true
            )
        )
    }
}