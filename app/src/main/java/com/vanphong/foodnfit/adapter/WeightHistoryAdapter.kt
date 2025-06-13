package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.UserProfiles
import com.vanphong.foodnfit.R

class WeightHistoryAdapter: ListAdapter<UserProfiles, WeightHistoryAdapter.WeightHistoryViewHolder>(WeightHistoryDiffCallback()) {
    class WeightHistoryViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val tvWeight: TextView = itemView.findViewById(R.id.tvWeight)
        val tvWeightDate: TextView = itemView.findViewById(R.id.tvWeightDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weight_history, parent, false)
        return WeightHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeightHistoryViewHolder, position: Int) {
        val profile = getItem(position)
        holder.tvWeight.text = profile.weight.toString()
        holder.tvWeightDate.text = profile.date.toString()
    }
}
class WeightHistoryDiffCallback: DiffUtil.ItemCallback<UserProfiles>(){
    override fun areItemsTheSame(oldItem: UserProfiles, newItem: UserProfiles): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserProfiles, newItem: UserProfiles): Boolean {
        return oldItem == newItem
    }

}