
package com.vanphong.foodnfit.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.FoodLogDetailAdapter // ** TẠO ADAPTER NÀY **
import com.vanphong.foodnfit.databinding.ActivityMealInformationBinding
import com.vanphong.foodnfit.model.FoodLogDetail
import com.vanphong.foodnfit.model.FoodLogResponse
import com.vanphong.foodnfit.model.UserGoalResponse
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.FoodLogViewModel
import java.util.Locale

class MealInformationActivity : BaseActivity() {
    private var _binding: ActivityMealInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodLogViewModel by viewModels()
    private lateinit var mealDetailAdapter: FoodLogDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMealInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.foodLogViewModel = viewModel

        val mealLogId = intent.getIntExtra("MEAL_LOG_ID", -1)
        if (mealLogId == -1) {
            Toast.makeText(this, "Error: Invalid Meal ID", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        // Yêu cầu ViewModel tải dữ liệu
        viewModel.loadMealInformation(mealLogId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        mealDetailAdapter = FoodLogDetailAdapter()
        binding.rvFoodLog.apply {
            adapter = mealDetailAdapter
            layoutManager = LinearLayoutManager(this@MealInformationActivity)
            // Ngăn RecyclerView tự cuộn, để NestedScrollView xử lý
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        // Khi cả hai mealInfo và userGoal đều có dữ liệu, cập nhật UI
        viewModel.mealInfo.observe(this) { mealData ->
            // Chỉ cập nhật UI nếu userGoal đã có dữ liệu
            viewModel.userGoal.value?.let { goalData ->
                updateUi(mealData, goalData)
            }
        }

        viewModel.userGoal.observe(this) { goalData ->
            // Chỉ cập nhật UI nếu mealInfo đã có dữ liệu
            viewModel.mealInfo.value?.let { mealData ->
                updateUi(mealData, goalData)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(this, "Loading details...")
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUi(mealData: FoodLogResponse, goalData: UserGoalResponse) {
        // Cập nhật tiêu đề toolbar
        binding.toolbar.title = mealData.meal.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }

        // Cập nhật tiêu đề log và số lượng món ăn
        binding.toolbar.title = getString(R.string.meal_log_title, mealData.meal) // Cần thêm string resource này
        binding.tvMealItemCount.text = mealData.foodLogDetails.size.toString()

        // Cập nhật các giá trị tổng
        binding.tvMealTotalCalories.text = mealData.totalCalories.toInt().toString()
        binding.tvMealCarbs.text = mealData.totalCarbs.toInt().toString()
        binding.tvMealFat.text = mealData.totalFat.toInt().toString()
        binding.tvMealProtein.text = mealData.totalProtein.toInt().toString()

        // Cập nhật progress bar và phần trăm
        updateNutrientProgress(binding.progressCalories, binding.tvCaloriesPercent, mealData.totalCalories, goalData.targetCalories)
        updateNutrientProgress(binding.progressCarbs, binding.tvCarbsPercent, mealData.totalCarbs, goalData.targetCarbs)
        updateNutrientProgress(binding.progressFat, binding.tvFatPercent, mealData.totalFat, goalData.targetFat)
        updateNutrientProgress(binding.progressProtein, binding.tvProteinPercent, mealData.totalProtein, goalData.targetProtein)

        // Cập nhật RecyclerView
        mealDetailAdapter.submitList(mealData.foodLogDetails)

        // Thiết lập biểu đồ
        setupPieChart(mealData)
    }

    // Hàm phụ để cập nhật progress bar
    private fun updateNutrientProgress(progressBar: android.widget.ProgressBar, textView: android.widget.TextView, current: Double, target: Double) {
        val percentage = if (target > 0) ((current / target) * 100).toInt() else 0
        progressBar.progress = percentage.coerceIn(0, 100)
        textView.text = "$percentage%"
    }

    private fun setupPieChart(data: FoodLogResponse) {
        val entries = ArrayList<PieEntry>()

        // Chỉ thêm các mục có giá trị lớn hơn 0 để tránh các lát cắt 0%
        if (data.totalCarbs > 0) {
            entries.add(PieEntry(data.totalCarbs.toFloat(), "Carbs"))
        }
        if (data.totalProtein > 0) {
            entries.add(PieEntry(data.totalProtein.toFloat(), "Protein"))
        }
        if (data.totalFat > 0) {
            entries.add(PieEntry(data.totalFat.toFloat(), "Fat"))
        }

        // Nếu không có dữ liệu dinh dưỡng, ẩn biểu đồ đi
        if (entries.isEmpty()) {
            binding.pieChartMeal.visibility = View.GONE
            return
        }
        binding.pieChartMeal.visibility = View.VISIBLE


        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(this@MealInformationActivity, R.color.color_carbs),
                ContextCompat.getColor(this@MealInformationActivity, R.color.color_protein),
                ContextCompat.getColor(this@MealInformationActivity, R.color.color_fat)
            )
            setDrawValues(false)
        }

        val pieData = PieData(dataSet)

        binding.pieChartMeal.apply {
            this.data = pieData

            // Vô hiệu hóa tất cả các nhãn và chú thích không cần thiết
            description.isEnabled = false   // Tắt mô tả biểu đồ
            legend.isEnabled = false        // Tắt chú thích (quan trọng)
            setDrawEntryLabels(false)       // Tắt nhãn của từng lát cắt (Carbs, Protein, Fat)

            // Cấu hình lỗ tròn ở giữa
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT) // Lỗ trong suốt
            holeRadius = 60f                // Tăng kích thước lỗ
            transparentCircleRadius = 0f    // Bỏ vòng tròn mờ xung quanh lỗ

            // Hiển thị tổng số Calo ở giữa (YÊU CẦU CHÍNH)
            centerText = "${data.totalCalories.toInt()}\nKcal"
            setCenterTextSize(24f)
            setCenterTextColor(Color.BLACK)

            // Không cần hiển thị giá trị dưới dạng phần trăm trên lát cắt
            setUsePercentValues(false)

            // Làm mới biểu đồ
            animateY(1000)
            invalidate()
        }
    }
}