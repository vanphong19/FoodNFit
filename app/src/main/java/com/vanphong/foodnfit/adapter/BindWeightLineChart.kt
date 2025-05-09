package com.vanphong.foodnfit.adapter

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.vanphong.foodnfit.Model.UserProfiles
import com.vanphong.foodnfit.R

@BindingAdapter("weightLineChart")
fun bindWeightLineChart(lineChart: LineChart, data: List<UserProfiles>){
    if (data.isEmpty()) return

    val entries = data.mapIndexed{index, userProfiles ->
        Entry(index.toFloat(), userProfiles.weight)
    }

    val drawable = ContextCompat.getDrawable(lineChart.context, R.drawable.linechart_gradient)
    val dataSet = LineDataSet(entries, "Cân nặng").apply {
        color = Color.parseColor("#ADFF2F")
        lineWidth = 3f
        setCircleColor(Color.parseColor("#ADFF2F"))   // viền xanh lá
        circleHoleColor = Color.WHITE               // bên trong trắng
        setDrawCircleHole(true)
        circleRadius = 5f
        circleHoleRadius = 3f

        valueTextSize = 0f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawFilled(true)
        fillDrawable = drawable
        fillAlpha = 255
    }

    lineChart.apply {
        this.data = LineData(dataSet)

        setBackgroundColor(Color.parseColor("#99C8E2B1"))
        // Tắt các thành phần không cần thiết
        description.isEnabled = false
        legend.isEnabled = false
        setTouchEnabled(false)
        setScaleEnabled(false)
        setPinchZoom(false)

        // Padding nội dung biểu đồ
        setViewPortOffsets(40f, 40f, 40f, 40f)

        // Tắt trục y
        axisLeft.isEnabled = false
        axisRight.isEnabled = false

        // Tuỳ chỉnh trục X
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            textColor = Color.DKGRAY

            // Format nhãn trục X
            val labels = buildList {
                add("Today")
                if (data.size > 2) {
                    addAll(List(data.size - 2) { "" }) // khoảng trống
                }
                add("End")
            }
            valueFormatter = IndexAxisValueFormatter(labels)
        }

        invalidate() // Vẽ lại
    }
}
