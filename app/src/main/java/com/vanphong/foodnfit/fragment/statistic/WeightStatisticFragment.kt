package com.vanphong.foodnfit.fragment.statistic

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.WeightHistoryAdapter
import com.vanphong.foodnfit.databinding.FragmentWeightStatisticBinding
import com.vanphong.foodnfit.model.MonthlyProfileResponse
import com.vanphong.foodnfit.model.WeightHistoryData
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.ProfilesViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

class WeightStatisticFragment : Fragment() {
    private var _binding: FragmentWeightStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var weightHistoryAdapter: WeightHistoryAdapter
    private val viewModel: ProfilesViewModel by viewModels()

    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    // Cờ để ngăn onItemSelected chạy trong lần thiết lập đầu tiên
    private var isInitialYearSetup = true
    private var isInitialMonthSetup = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeightStatisticBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.profilesViewModel = viewModel

        initRvWeightHistory()
        setupDateSpinners()
        observeViewModel()

        // Tải dữ liệu lần đầu tiên sau khi mọi thứ đã được thiết lập
        loadMonthlyStatistics()

        return binding.root
    }

    private fun setupDateSpinners() {
        setupYearSpinner()
        setupMonthSpinner()
    }

    private fun setupYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (2020..currentYear).map { it.toString() }.reversed()
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = yearAdapter

        // 1. Gắn listener TRƯỚC
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Nếu cờ là true, đây là lần chạy setup. Tiêu thụ cờ và thoát.
                if (isInitialYearSetup) {
                    isInitialYearSetup = false
                    return
                }

                val newlySelectedYear = years[position].toInt()
                if (selectedYear != newlySelectedYear) {
                    selectedYear = newlySelectedYear
                    setupMonthSpinner() // Thiết lập lại tháng
                    loadMonthlyStatistics() // Tải dữ liệu mới
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val currentYearIndex = years.indexOf(selectedYear.toString())
        if (currentYearIndex != -1) {
            // 2. Đặt cờ thành true
            isInitialYearSetup = true
            // 3. Gọi setSelection VỚI tham số true để chắc chắn listener được kích hoạt
            binding.spinnerYear.setSelection(currentYearIndex, true)
        }
    }

    // --- SỬA ĐỔI Ở ĐÂY ---
    private fun setupMonthSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val monthNames = resources.getStringArray(R.array.months_full)

        val lastMonth = if (selectedYear == currentYear) currentMonth else 12
        val availableMonths = monthNames.take(lastMonth)

        if (selectedMonth > lastMonth) {
            selectedMonth = lastMonth
        }

        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableMonths)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMonth.adapter = monthAdapter

        // 1. Gắn listener TRƯỚC
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Nếu cờ là true, đây là lần chạy setup. Tiêu thụ cờ và thoát.
                if (isInitialMonthSetup) {
                    isInitialMonthSetup = false
                    return
                }

                val newlySelectedMonth = position + 1
                if (selectedMonth != newlySelectedMonth) {
                    selectedMonth = newlySelectedMonth
                    loadMonthlyStatistics()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (selectedMonth > 0 && selectedMonth -1 < availableMonths.size) {
            // 2. Đặt cờ thành true
            isInitialMonthSetup = true
            // 3. Gọi setSelection VỚI tham số true để chắc chắn listener được kích hoạt
            binding.spinnerMonth.setSelection(selectedMonth - 1, true)
        }
    }

    private fun loadMonthlyStatistics() {
        // Xóa dữ liệu cũ trên UI để người dùng biết là đang tải mới
        clearUI()
        // Gọi ViewModel để lấy dữ liệu
        lifecycleScope.launch {
            viewModel.getMonthlyStatistics(selectedYear, selectedMonth)
        }
    }

    private fun observeViewModel() {
        // Observe monthly statistics
        viewModel.monthlyStatistics.observe(viewLifecycleOwner) { response ->
            if (response != null && response.weightHistory.isNotEmpty()) {
                updateUI(response)
            } else {
                clearUI()
                Toast.makeText(requireContext(), getString(R.string.no_data_for_this_month), Toast.LENGTH_SHORT).show()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(requireActivity(), getString(R.string.loading))
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun clearUI() {
        binding.tvInitialWeight.text = "0"
        binding.tvCurrentWeight.text = "0"
        binding.tvChangeWeight.text = "0"
        binding.tvCurrentWeightDisplay.text = "0 kg"
        binding.tvBmiStatus.text = "N/A"
        updateBmiIndicator(0f)
        binding.lineChart.clear()
        binding.lineChart.invalidate()
        weightHistoryAdapter.submitList(emptyList())
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(response: MonthlyProfileResponse) {
        binding.tvInitialWeight.text = response.initialWeight.roundToInt().toString()
        binding.tvCurrentWeight.text = response.currentWeight.roundToInt().toString()
        binding.tvChangeWeight.text = abs(response.weightChange).roundToInt().toString()
        binding.tvCurrentWeightDisplay.text = "${response.currentWeight.roundToInt()} kg"
        binding.tvBmiStatus.text = response.bmiStatus

        updateBmiIndicator(response.currentBmi)
        updateWeightChangeIcon(response.weightChange)

        // Cập nhật biểu đồ và RecyclerView
        setupLineChart(response.weightHistory)
        viewModel.setWeightHistories(response.weightHistory)
    }

    // ... Các hàm khác giữ nguyên ...

    private fun updateBmiIndicator(bmi: Float) {
        val constraintLayout = binding.bmiLayout
        val guideline = binding.indicatorGuideline

        val layoutParams =
            guideline.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

        val percentage = when {
            bmi <= 0f -> 0.0f // Handle no data case
            bmi < 15f -> 0.0f
            bmi < 18.5f -> (bmi - 15f) / (18.5f - 15f) * 0.175f
            bmi < 22.9f -> 0.175f + (bmi - 18.5f) / (22.9f - 18.5f) * 0.195f
            bmi < 24.9f -> 0.37f + (bmi - 22.9f) / (24.9f - 22.9f) * 0.1f
            bmi < 29.9f -> 0.47f + (bmi - 24.9f) / (29.9f - 24.9f) * 0.25f
            bmi < 35f -> 0.72f + (bmi - 29.9f) / (35f - 29.9f) * 0.23f
            else -> 0.95f
        }

        layoutParams.guidePercent = percentage
        guideline.layoutParams = layoutParams
    }

    private fun updateWeightChangeIcon(weightChange: Float) {
        val imageView = binding.ivWeightChangeIcon
        val textView = binding.tvChangeWeight

        if (weightChange >= 0) {
            imageView.setImageResource(R.drawable.ic_increase)
            textView.setTextColor(Color.parseColor("#00CC00"))
        } else {
            imageView.setImageResource(R.drawable.ic_decrease)
            textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        }
    }

    private fun setupLineChart(weightHistory: List<WeightHistoryData>) {
        val lineChart = binding.lineChart

        if (weightHistory.isEmpty()) {
            lineChart.clear()
            lineChart.invalidate()
            return
        }

        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        weightHistory.forEachIndexed { index, data ->
            entries.add(Entry(index.toFloat(), data.weight))
            // Định dạng ngày: "DD/MM"
            val dateParts = data.date.split("-")
            if (dateParts.size == 3) {
                labels.add("${dateParts[2]}/${dateParts[1]}")
            } else {
                labels.add(data.date) // Fallback
            }
        }

        val dataSet = LineDataSet(entries, "Cân nặng (kg)")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.white)
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.white))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 100
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.white)
        xAxis.granularity = 1f

        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.white)
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.gridColor = ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
        lineChart.axisRight.isEnabled = false

        lineChart.animateX(1000)
        lineChart.invalidate()
    }

    private fun initRvWeightHistory() {
        weightHistoryAdapter = WeightHistoryAdapter()
        binding.rvWeightHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWeightHistory.adapter = weightHistoryAdapter

        // Dữ liệu cho RV giờ sẽ được cập nhật thông qua viewModel.setWeightHistories() trong hàm updateUI
        viewModel.weightHistories.observe(viewLifecycleOwner) { list ->
            weightHistoryAdapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}