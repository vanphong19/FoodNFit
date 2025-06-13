package com.vanphong.foodnfit.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.util.dpToPx

class NumberPickerAdapter(
    private val numbers: List<Int>,
    var centerPosition: Int = 0
) : RecyclerView.Adapter<NumberPickerAdapter.NumberViewHolder>() {

    inner class NumberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_picker_number, parent, false)
        return NumberViewHolder(view)
    }

    override fun getItemCount() = numbers.size

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        val number = numbers[position]
        holder.tvNumber.text = number.toString()

        val params = holder.tvNumber.layoutParams
        val context = holder.itemView.context

        if (position == centerPosition) {
            holder.tvNumber.setBackgroundResource(R.drawable.bg_picker_selected)
            holder.tvNumber.setTextColor(Color.parseColor("#263238"))
            holder.tvNumber.textSize = 28f
            params.height = 120.dpToPx(context)
            holder.tvNumber.setTypeface(null, Typeface.BOLD)
        } else {
            holder.tvNumber.setBackgroundResource(R.drawable.bg_picker_normal)
            holder.tvNumber.setTextColor(Color.parseColor("#90A4AE"))
            holder.tvNumber.textSize = 20f
            params.height = 80.dpToPx(context)
            holder.tvNumber.setTypeface(null, Typeface.NORMAL)
        }
        holder.tvNumber.layoutParams = params
    }

    fun updateCenterPosition(newPos: Int) {
        val oldPos = centerPosition
        centerPosition = newPos
        notifyItemChanged(oldPos)
        notifyItemChanged(newPos)
    }
}
