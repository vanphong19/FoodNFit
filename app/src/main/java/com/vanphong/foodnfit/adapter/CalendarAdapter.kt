package com.vanphong.foodnfit.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.util.CalendarUtils
import java.time.LocalDate

class CalendarAdapter(
    private val days: List<LocalDate?>,
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String, date: LocalDate?)
    }

    inner class CalendarViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val dayText = dayOfMonth.text.toString()
                val date = days.getOrNull(position)
                onItemListener.onItemClick(position, dayText, date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        if (date == null) {
            holder.dayOfMonth.text = ""
        } else {
            holder.dayOfMonth.text = date.dayOfMonth.toString()
            if (date == CalendarUtils.selectedDate) {
                holder.dayOfMonth.setBackgroundColor(Color.LTGRAY)
            } else {
                holder.dayOfMonth.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun getItemCount(): Int = days.size
}
