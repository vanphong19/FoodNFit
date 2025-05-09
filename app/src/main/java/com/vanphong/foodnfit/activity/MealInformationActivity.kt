package com.vanphong.foodnfit.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.Model.FoodLog
import com.vanphong.foodnfit.Model.FoodLogDetail
import com.vanphong.foodnfit.adapter.FoodLogAdapter
import com.vanphong.foodnfit.databinding.ActivityMealInformationBinding
import com.vanphong.foodnfit.viewModel.FoodLogViewModel
import java.time.LocalDate
import java.util.Date
import java.util.UUID

class MealInformationActivity : BaseActivity() {
    private var _binding: ActivityMealInformationBinding? = null
    private lateinit var foodLogAdapter: FoodLogAdapter
    private val binding get() = _binding!!
    private val viewModel: FoodLogViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMealInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.foodLogViewModel = viewModel

        foodLogAdapter = FoodLogAdapter()
        binding.rvFoodLog.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFoodLog.adapter = foodLogAdapter

        viewModel.foodLog.observe(this){foodLog ->
            foodLogAdapter.submitList(listOf( foodLog))
        }
        val log = getFoodLog()
        viewModel.setFoodLog(log)
    }

    private fun getFoodLog(): FoodLog {
        val detail1 = FoodLogDetail(1, 1, 101, "Trứng", "1 quả", 75.0, 6.0, 1.0, 5.0)
        val detail2 = FoodLogDetail(2, 1, 102, "Cơm trắng", "1 chén", 200.0, 4.0, 44.0, 0.5)

        return FoodLog(
            1,
            UUID.randomUUID(),
            275.0,
            LocalDate.now(),
            10.0,
            5.5,
            45.0,
            "Bữa sáng",
            listOf(detail1, detail2)
        )
    }
}