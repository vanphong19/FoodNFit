package com.vanphong.foodnfit.adapter

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.R

@BindingAdapter("pieChartData")
fun bindPieChart(pieChart: PieChart, foodItem: FoodItem){
    val data = foodItem?: return

    val entries = listOf(
        PieEntry(data.carbs ?: 0f, pieChart.context.getString(R.string.carbs)),
        PieEntry(data.protein ?: 0f, pieChart.context.getString(R.string.protein)),
        PieEntry(data.fat ?: 0f, pieChart.context.getString(R.string.fat))
    )

    val dataSet = PieDataSet(entries,"")
    dataSet.setColors(
        ContextCompat.getColor(pieChart.context, R.color.color_carbs),
                ContextCompat.getColor(pieChart.context, R.color.color_protein),
        ContextCompat.getColor(pieChart.context, R.color.color_fat)
    )

    dataSet.valueTextSize = 14f
    val pieData = PieData(dataSet)
    pieData.setValueFormatter(PercentFormatter(pieChart))
    pieChart.data = pieData
    pieChart.setEntryLabelColor(Color.DKGRAY)
    pieChart.description.isEnabled = false
    pieChart.legend.isEnabled = false
    pieChart.setUsePercentValues(true)
    pieChart.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)
    pieChart.invalidate()
}