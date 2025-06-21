package com.vanphong.foodnfit.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.HourlyStepSummary

class StepDetailDialog(
    context: Context,
    private val hourlyData: List<HourlyStepSummary>
) : Dialog(context) {

    private lateinit var barChart: BarChart
    private lateinit var tvTotalSteps: TextView
    private lateinit var tvTotalDistance: TextView
    private lateinit var btnClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_step_detail, null)
        setContentView(view)

        initViews(view)
        setupChart()
        loadData()

        btnClose.setOnClickListener { dismiss() }

        // Thiết lập kích thước dialog
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            (context.resources.displayMetrics.heightPixels * 0.7).toInt()
        )
    }

    private fun initViews(view: android.view.View) {
        barChart = view.findViewById(R.id.barChart)
        tvTotalSteps = view.findViewById(R.id.tvTotalSteps)
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance)
        btnClose = view.findViewById(R.id.btnClose)
    }

    private fun setupChart() {
        barChart.apply {
            // Thiết lập cơ bản
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setMaxVisibleValueCount(24)

            // Thiết lập trục X (giờ)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = 24
                textColor = Color.BLACK
                textSize = 10f
                labelRotationAngle = -45f
                setAvoidFirstLastClipping(true)
            }

            // Thiết lập trục Y trái (số bước)
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
                textColor = Color.BLACK
                textSize = 10f
                axisMinimum = 0f
            }

            // Ẩn trục Y phải
            axisRight.isEnabled = false

            // Thiết lập màu nền
            setBackgroundColor(Color.WHITE)
        }
    }

    private fun loadData() {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        var totalSteps = 0
        var totalDistance = 0.0

        // Tạo dữ liệu cho chart
        hourlyData.forEachIndexed { index, data ->
            entries.add(BarEntry(index.toFloat(), data.totalSteps.toFloat()))
            labels.add(String.format("%02d:00", data.hour))
            totalSteps += data.totalSteps
            totalDistance += data.totalDistance
        }

        // Tạo dataset
        val dataSet = BarDataSet(entries, "Bước chân").apply {
            color = context.resources.getColor(R.color.blue_pastel, null)
            valueTextColor = Color.BLACK
            valueTextSize = 8f
            setDrawValues(true)
        }

        // Thiết lập dữ liệu cho chart
        val barData = BarData(dataSet)
        barData.barWidth = 0.8f

        barChart.data = barData
        barChart.xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index % 2 == 1) String.format("%02d:00", index) else ""
            }
        }

        barChart.invalidate() // refresh chart

        // Cập nhật tổng kết
        tvTotalSteps.text = totalSteps.toString()
        tvTotalDistance.text = String.format("%.1f m", totalDistance)
    }
}