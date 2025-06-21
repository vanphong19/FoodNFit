package com.vanphong.foodnfit.fragment.statistic

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.CaloriesStatisticAdapter

import com.vanphong.foodnfit.databinding.FragmentCaloriesBinding
import com.vanphong.foodnfit.model.DailyCalorieDataDto
import com.vanphong.foodnfit.model.MealTypeSummaryDto
import com.vanphong.foodnfit.model.WeeklyNutritionResponse
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.CaloriesStatisticViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

class CaloriesFragment : Fragment() {
    private var _binding: FragmentCaloriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CaloriesStatisticViewModel by viewModels()
    private lateinit var caloriesStatisticAdapter: CaloriesStatisticAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.caloriesViewModel = viewModel

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        caloriesStatisticAdapter = CaloriesStatisticAdapter()
        binding.rvLogs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLogs.adapter = caloriesStatisticAdapter
    }

    private fun setupClickListeners() {
        binding.btnPreviousWeek.setOnClickListener {
            viewModel.onPreviousWeekClicked()
        }
        binding.btnNextWeek.setOnClickListener {
            viewModel.onNextWeekClicked()
        }
        binding.tvWeekDisplay.setOnClickListener {
            showDatePicker()
        }
    }

    // Trong file CaloriesFragment.kt

    private fun observeViewModel() {
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            updateWeekDisplay(date)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(requireActivity(), getString(R.string.loading))
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }

        viewModel.weeklyNutritionData.observe(viewLifecycleOwner) { data ->
            data?.let {
                updateUi(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                binding.tvTotalWeeklyCalories.text = "Error"
                resetUi()
            }
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                resetUi() // Dọn dẹp giao diện
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun resetUi() {
        Log.d("CaloriesFragment", "Resetting UI to initial state.")
        binding.tvTotalWeeklyCalories.text = "0 kcal"

        // Reset thông tin các bữa ăn
        binding.tvCaloBreakfast.text = "0"
        binding.tvPercentBreakfast.text = "0%"
        binding.progressBreakfast.progress = 0

        binding.tvCaloLunch.text = "0"
        binding.tvPercentLunch.text = "0%"
        binding.progressLunch.progress = 0

        binding.tvCaloDinner.text = "0"
        binding.tvPercentDinner.text = "0%"
        binding.progressDinner.progress = 0

        binding.tvCaloSnack.text = "0"
        binding.tvPercentSnack.text = "0%"
        binding.progressSnack.progress = 0

        // Xóa dữ liệu của biểu đồ
        binding.lineChart.clear()
        binding.lineChart.invalidate()

        // Xóa dữ liệu của RecyclerView
        caloriesStatisticAdapter.submitList(emptyList())
    }

    private fun updateUi(data: WeeklyNutritionResponse) {
        // Cập nhật tổng calories tuần
        binding.tvTotalWeeklyCalories.text = "${data.totalWeeklyCalories.roundToInt()} kcal"

        // Cập nhật thông tin các bữa ăn
        updateMealSummaries(data.mealSummaries)

        // Cập nhật RecyclerView logs
        caloriesStatisticAdapter.submitList(data.dailyCalorieChartData)

        // Cập nhật biểu đồ LineChart
        setupLineChart(data.dailyCalorieChartData)
    }

    @SuppressLint("SetTextI18n")
    private fun updateMealSummaries(summaries: List<MealTypeSummaryDto>) {
        summaries.forEach { summary ->
            when (summary.mealType.uppercase()) {
                "BREAKFAST" -> {
                    binding.tvCaloBreakfast.text = summary.totalCalories.roundToInt().toString()
                    binding.tvPercentBreakfast.text = "${summary.percentage.roundToInt()}%"
                    binding.progressBreakfast.progress = summary.percentage.roundToInt()
                }
                "LUNCH" -> {
                    binding.tvCaloLunch.text = summary.totalCalories.roundToInt().toString()
                    binding.tvPercentLunch.text = "${summary.percentage.roundToInt()}%"
                    binding.progressLunch.progress = summary.percentage.roundToInt()
                }
                "DINNER" -> {
                    binding.tvCaloDinner.text = summary.totalCalories.roundToInt().toString()
                    binding.tvPercentDinner.text = "${summary.percentage.roundToInt()}%"
                    binding.progressDinner.progress = summary.percentage.roundToInt()
                }
                "SNACK" -> {
                    binding.tvCaloSnack.text = summary.totalCalories.roundToInt().toString()
                    binding.tvPercentSnack.text = "${summary.percentage.roundToInt()}%"
                    binding.progressSnack.progress = summary.percentage.roundToInt()
                }
            }
        }
    }

    private fun setupLineChart(dailyData: List<DailyCalorieDataDto>) {
        val entries = dailyData.mapIndexed { index, data ->
            Entry(index.toFloat(), data.totalCalories.toFloat())
        }

        val dataSet = LineDataSet(entries, "Daily Calories").apply {
            color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setCircleColor(color)
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(false) // Không hiển thị giá trị trên các điểm
            mode = LineDataSet.Mode.CUBIC_BEZIER // Bo tròn đường kẻ
        }

        val lineData = LineData(dataSet)
        binding.lineChart.apply {
            data = lineData
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    private val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                        return days.getOrNull(value.toInt()) ?: ""
                    }
                }
            }
            invalidate() // Refresh chart
        }
    }

    private fun updateWeekDisplay(date: LocalDate) {
        val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        val formatter = DateTimeFormatter.ofPattern("MMM dd")
        val weekText = if (startOfWeek.year == endOfWeek.year) {
            "${startOfWeek.format(formatter)} - ${endOfWeek.format(formatter)}, ${startOfWeek.year}"
        } else {
            "${startOfWeek.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}"
        }
        binding.tvWeekDisplay.text = weekText
    }

    private fun showDatePicker() {
        val selectedDateInMillis = viewModel.selectedDate.value
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
            ?: Instant.now().toEpochMilli()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(selectedDateInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedLocalDate = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            viewModel.onDateSelected(selectedLocalDate)
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshToCurrentDate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}