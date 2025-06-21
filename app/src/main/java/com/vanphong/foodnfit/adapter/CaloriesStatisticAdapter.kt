package com.vanphong.foodnfit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.model.DailyCalories
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.DailyCalorieDataDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class CaloriesStatisticAdapter: ListAdapter<DailyCalorieDataDto, CaloriesStatisticAdapter.CaloriesStatisticViewHolder>(CaloriesStatisticDiffCallback()) {
    class CaloriesStatisticViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val tvDateCalories: TextView = itemView.findViewById(R.id.tv_date_calories)
        val tvCaloCalories: TextView = itemView.findViewById(R.id.tv_calo_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaloriesStatisticViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log_statistic, parent, false)
        return CaloriesStatisticViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CaloriesStatisticViewHolder, position: Int) {
        val log = getItem(position)
        val date = LocalDate.parse(log.date)
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM dd")
        holder.tvDateCalories.text = date.format(formatter)
        holder.tvCaloCalories.text = "${log.totalCalories.roundToInt()} kcal"
    }
}
class CaloriesStatisticDiffCallback: DiffUtil.ItemCallback<DailyCalorieDataDto>(){
    override fun areItemsTheSame(oldItem: DailyCalorieDataDto, newItem: DailyCalorieDataDto): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyCalorieDataDto, newItem: DailyCalorieDataDto): Boolean {
        return oldItem == newItem
    }
}