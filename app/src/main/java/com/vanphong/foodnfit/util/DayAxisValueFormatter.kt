package com.vanphong.foodnfit.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class DayAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val index = value.toInt()
        return if (index >= 0 && index < labels.size) {
            labels[index]
        } else {
            ""
        }
    }
}