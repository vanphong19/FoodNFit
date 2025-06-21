package com.vanphong.foodnfit.adapter

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.vanphong.foodnfit.model.FoodItem
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.FoodItemResponse

@BindingAdapter("pieChartData")
fun bindPieChart(pieChart: PieChart, foodItem: FoodItemResponse?) {
    val data = foodItem ?: return

    val entries = listOf(
        PieEntry(data.carbs.toFloat(), pieChart.context.getString(R.string.carbs)),
        PieEntry(data.protein.toFloat(), pieChart.context.getString(R.string.protein)),
        PieEntry(data.fat.toFloat(), pieChart.context.getString(R.string.fat))
    )

    val dataSet = PieDataSet(entries, "")
    dataSet.setColors(
        ContextCompat.getColor(pieChart.context, R.color.color_carbs),
        ContextCompat.getColor(pieChart.context, R.color.color_protein),
        ContextCompat.getColor(pieChart.context, R.color.color_fat)
    )
    dataSet.valueTextColor = Color.WHITE
    dataSet.valueTextSize = 12f

    val pieData = PieData(dataSet)
    pieData.setValueFormatter(PercentFormatter(pieChart))

    pieChart.data = pieData
    pieChart.setUsePercentValues(true)
    pieChart.description.isEnabled = false
    pieChart.legend.isEnabled = false

    pieChart.isDrawHoleEnabled = true
    pieChart.holeRadius = 75f
    pieChart.transparentCircleRadius = 0f
    pieChart.setDrawEntryLabels(false)


    pieChart.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)
    pieChart.invalidate()
}
