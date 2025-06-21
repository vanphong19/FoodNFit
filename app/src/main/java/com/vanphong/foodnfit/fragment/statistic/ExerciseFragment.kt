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
import com.vanphong.foodnfit.adapter.FavouriteExerciseAdapter
import com.vanphong.foodnfit.adapter.WorkoutLogAdapter
import com.vanphong.foodnfit.databinding.FragmentExercise2Binding
import com.vanphong.foodnfit.model.DailyCaloriesExerciseDto
import com.vanphong.foodnfit.model.WeeklyExerciseSummaryResponse
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.WorkoutPlanViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

class ExerciseFragment : Fragment() {
    private var _binding: FragmentExercise2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: WorkoutPlanViewModel by viewModels()

    private lateinit var workoutLogAdapter: WorkoutLogAdapter
    private lateinit var favouriteExerciseAdapter: FavouriteExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercise2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.workoutViewModel = viewModel

        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshToCurrentDate()
    }

    private fun setupRecyclerViews() {
        // Workout Log Adapter
        workoutLogAdapter = WorkoutLogAdapter()
        binding.rvWorkoutLog.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWorkoutLog.adapter = workoutLogAdapter

        // Favourite Exercise Adapter
        favouriteExerciseAdapter = FavouriteExerciseAdapter()
        binding.rvFavourExercise.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavourExercise.adapter = favouriteExerciseAdapter
    }

    private fun setupClickListeners() {
        binding.btnPreviousWeek.setOnClickListener { viewModel.onPreviousWeekClicked() }
        binding.btnNextWeek.setOnClickListener { viewModel.onNextWeekClicked() }
        binding.tvWeekDisplay.setOnClickListener { showDatePicker() }
    }

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

        viewModel.weeklySummary.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                updateUi(data)
            }
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                Toast.makeText(context, "Không có dữ liệu cho tuần này", Toast.LENGTH_SHORT).show()
                resetUi()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                resetUi()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(data: WeeklyExerciseSummaryResponse) {
        // Update top card
        binding.tvTotalCaloriesBurnt.text = "${data.totalCaloriesBurnt.roundToInt()} " + getString(R.string.kcal_burnt)

        // Update summary cards
        binding.tvTotalSessions.text = data.totalTrainingSessions.toString()
        binding.tvTotalSteps.text = data.totalSteps.toString()
        binding.tvAvgCalories.text = data.averageCaloriesPerSession.roundToInt().toString()
        binding.tvBestDay.text = data.bestDay.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }

        // Update LineChart
        setupLineChart(data.dailyCalorieChartData)

        // Update RecyclerViews
        favouriteExerciseAdapter.submitList(data.favoriteExercises)
        workoutLogAdapter.submitList(data.dailyCalorieChartData.filter { it.exerciseCount > 0 })
    }

    @SuppressLint("SetTextI18n")
    private fun resetUi() {
        Log.d("ExerciseFragment", "Resetting UI to initial state.")
        binding.tvTotalCaloriesBurnt.text = "0 kcal burnt"
        binding.tvTotalSessions.text = "0"
        binding.tvTotalSteps.text = "0"
        binding.tvAvgCalories.text = "0"
        binding.tvBestDay.text = "N/A"

        binding.lineChart.clear()
        binding.lineChart.invalidate()

        favouriteExerciseAdapter.submitList(emptyList())
        workoutLogAdapter.submitList(emptyList())
    }

    private fun setupLineChart(dailyData: List<DailyCaloriesExerciseDto>) {
        val entries = dailyData.mapIndexed { index, data ->
            Entry(index.toFloat(), data.totalCalories.toFloat())
        }
        val dataSet = LineDataSet(entries, "Calories Burnt").apply {
            color = ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setCircleColor(color)
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(false) // Không hiển thị giá trị trên các điểm
            mode = LineDataSet.Mode.CUBIC_BEZIER
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
            invalidate()
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}