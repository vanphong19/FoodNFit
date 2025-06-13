package com.vanphong.foodnfit.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.HistoryResponse
import com.vanphong.foodnfit.Model.UserResponseById
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.util.DateUtils
import java.time.format.DateTimeFormatter

class UserHistoryAdapter: ListAdapter<HistoryResponse, UserHistoryAdapter.UserHistoryViewHolder>(UserHistoryDiffCallback()) {
    class UserHistoryViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvUpdatedDate: TextView = itemView.findViewById(R.id.tvUpdatedDate)
        val tvUpdatedType: TextView = itemView.findViewById(R.id.tvUpdatedType)
        val tvUpdater: TextView = itemView.findViewById(R.id.tvUpdater)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_history, parent, false)
        return UserHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserHistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.tvUpdatedDate.text = DateUtils.formatDateString(history.changedAt,    outputPattern = "dd/MM/yyyy"
        )

        holder.tvUpdatedType.text = history.changeType
        holder.tvUpdater.text = history.changedBy
    }
}
class UserHistoryDiffCallback: DiffUtil.ItemCallback<HistoryResponse>(){
    override fun areItemsTheSame(oldItem: HistoryResponse, newItem: HistoryResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HistoryResponse, newItem: HistoryResponse): Boolean {
        return oldItem == newItem
    }

}