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
import com.vanphong.foodnfit.Model.Reminder
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.util.getRelativeTimeString

class NotificationAdapter: ListAdapter<Reminder, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {
    class NotificationViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvNotification: TextView = itemView.findViewById(R.id.tvNotification)
        val tvNotificationTime: TextView = itemView.findViewById(R.id.tvNotificationTime)
        val btnMore: ImageView = itemView.findViewById(R.id.img_threeDot)
        val rlNotification: RelativeLayout = itemView.findViewById(R.id.rlNotification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.tvNotification.text = reminder.message.toString()
        holder.tvNotificationTime.text = reminder.scheduledTime?.let { getRelativeTimeString(it) } ?: ""
        if(reminder.isRead){
            holder.rlNotification.background = ColorDrawable(Color.TRANSPARENT)
        }
        else holder.rlNotification.background = ContextCompat.getDrawable(holder.rlNotification.context, R.drawable.bg_notification_gray)
    }
}
class NotificationDiffCallback: DiffUtil.ItemCallback<Reminder>(){
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }

}