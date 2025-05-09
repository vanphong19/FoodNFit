package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.DailyCalories
import com.vanphong.foodnfit.R

class CaloriesStatisticAdapter: ListAdapter<DailyCalories, CaloriesStatisticAdapter.CaloriesStatisticViewHolder>(CaloriesStatisticDiffCallback()) {
    class CaloriesStatisticViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val tvDateCalories: TextView = itemView.findViewById(R.id.tv_date_calories)
        val tvCaloCalories: TextView = itemView.findViewById(R.id.tv_calo_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaloriesStatisticViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log_statistic, parent, false)
        return CaloriesStatisticViewHolder(view)
    }

    override fun onBindViewHolder(holder: CaloriesStatisticViewHolder, position: Int) {
        val log = getItem(position)
        holder.tvDateCalories.text = log.date.toString()
        holder.tvCaloCalories.text = log.totalCalorie.toString()
    }
}
class CaloriesStatisticDiffCallback: DiffUtil.ItemCallback<DailyCalories>(){
    override fun areItemsTheSame(oldItem: DailyCalories, newItem: DailyCalories): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyCalories, newItem: DailyCalories): Boolean {
        return oldItem == newItem
    }
}