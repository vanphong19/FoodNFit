package com.vanphong.foodnfit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.WeightHistoryData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt

class WeightHistoryAdapter : ListAdapter<WeightHistoryData, WeightHistoryAdapter.WeightHistoryViewHolder>(WeightHistoryDiffCallback()) {

    class WeightHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWeight: TextView = itemView.findViewById(R.id.tvWeight)
        val tvWeightDate: TextView = itemView.findViewById(R.id.tvWeightDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weight_history, parent, false)
        return WeightHistoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeightHistoryViewHolder, position: Int) {
        val historyItem = getItem(position)

        holder.tvWeight.text = "${historyItem.weight.roundToInt()} kg"

        try {
            val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM")
            val date = LocalDate.parse(historyItem.date, inputFormatter)
            holder.tvWeightDate.text = date.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            holder.tvWeightDate.text = historyItem.date
        }
    }
}

class WeightHistoryDiffCallback : DiffUtil.ItemCallback<WeightHistoryData>() {
    override fun areItemsTheSame(oldItem: WeightHistoryData, newItem: WeightHistoryData): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: WeightHistoryData, newItem: WeightHistoryData): Boolean {
        return oldItem == newItem
    }
}