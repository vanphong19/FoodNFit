package com.vanphong.foodnfit.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.model.Reminder
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.ReminderResponse
import com.vanphong.foodnfit.util.getRelativeTimeString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class NotificationAdapter(val onItemClick: (Int) -> Unit): ListAdapter<ReminderResponse, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {
    class NotificationViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvNotification: TextView = itemView.findViewById(R.id.tvNotification)
        val tvNotificationTime: TextView = itemView.findViewById(R.id.tvNotificationTime)
        val btnMore: ImageView = itemView.findViewById(R.id.img_threeDot)
        val rlNotification: RelativeLayout = itemView.findViewById(R.id.rlNotification)
        val imgNotification: ImageView = itemView.findViewById(R.id.img_notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.tvNotification.text = reminder.message

        // Parse String -> LocalDateTime
        val dateTime = parseDateTime(reminder.scheduledTime)
        val relativeTime = dateTime?.let { getRelativeTimeString(it) } ?: ""
        val imageRes = if (reminder.frequency == "analyse") {
            R.drawable.ic_notification_ai
        } else {
            R.drawable.ic_notification
        }

        holder.imgNotification.setImageResource(imageRes)
        holder.rlNotification.setOnClickListener {
            onItemClick(reminder.id)
        }

        holder.tvNotificationTime.text = relativeTime
    }

    private fun parseDateTime(dateTimeStr: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
class NotificationDiffCallback: DiffUtil.ItemCallback<ReminderResponse>(){
    override fun areItemsTheSame(oldItem: ReminderResponse, newItem: ReminderResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ReminderResponse, newItem: ReminderResponse): Boolean {
        return oldItem == newItem
    }

}