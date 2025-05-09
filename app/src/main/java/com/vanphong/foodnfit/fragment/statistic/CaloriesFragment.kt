package com.vanphong.foodnfit.fragment.statistic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.Model.DailyCalories
import com.vanphong.foodnfit.Model.FoodLog
import com.vanphong.foodnfit.adapter.CaloriesStatisticAdapter
import com.vanphong.foodnfit.databinding.FragmentCaloriesBinding
import com.vanphong.foodnfit.viewModel.CaloriesStatisticViewModel
import java.time.LocalDate
import java.util.UUID

class CaloriesFragment : Fragment() {
    private var _binding: FragmentCaloriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CaloriesStatisticViewModel by viewModels()
    private lateinit var caloriesStatisticAdapter: CaloriesStatisticAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.caloriesViewModel = viewModel

        caloriesStatisticAdapter = CaloriesStatisticAdapter()
        viewModel.dailyCaloriesList.observe(requireActivity()){dataList ->
            caloriesStatisticAdapter.submitList(dataList)
        }

        val data = getData()
        binding.rvLogs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvLogs.adapter = caloriesStatisticAdapter

        viewModel.setDailyCaloriesList(data)

        return binding.root
    }

    private fun getData(): List<DailyCalories> {
        val userId = UUID.randomUUID()
        return listOf(
            DailyCalories(
                todayMeal = listOf(
                    FoodLog(
                        1,
                        userId,
                        350.0,
                        LocalDate.now().minusDays(4),
                        15.0,
                        10.0,
                        45.0,
                        "Breakfast",
                        listOf()
                    ),
                    FoodLog(
                        2,
                        userId,
                        600.0,
                        LocalDate.now().minusDays(4),
                        25.0,
                        20.0,
                        80.0,
                        "Lunch",
                        listOf()
                    ),
                    FoodLog(
                        3,
                        userId,
                        450.0,
                        LocalDate.now().minusDays(4),
                        20.0,
                        15.0,
                        60.0,
                        "Dinner",
                        listOf()
                    )
                )
            ),
            DailyCalories(
                todayMeal = listOf(
                    FoodLog(
                        4,
                        userId,
                        300.0,
                        LocalDate.now().minusDays(3),
                        10.0,
                        8.0,
                        40.0,
                        "Breakfast",
                        listOf()
                    ),
                    FoodLog(
                        5,
                        userId,
                        650.0,
                        LocalDate.now().minusDays(3),
                        30.0,
                        25.0,
                        90.0,
                        "Lunch",
                        listOf()
                    ),
                    FoodLog(
                        6,
                        userId,
                        500.0,
                        LocalDate.now().minusDays(3),
                        22.0,
                        18.0,
                        65.0,
                        "Dinner",
                        listOf()
                    )
                )
            ),
            DailyCalories(
                todayMeal = listOf(
                    FoodLog(
                        7,
                        userId,
                        320.0,
                        LocalDate.now().minusDays(2),
                        12.0,
                        9.0,
                        42.0,
                        "Breakfast",
                        listOf()
                    ),
                    FoodLog(
                        8,
                        userId,
                        700.0,
                        LocalDate.now().minusDays(2),
                        35.0,
                        30.0,
                        100.0,
                        "Lunch",
                        listOf()
                    ),
                    FoodLog(
                        9,
                        userId,
                        480.0,
                        LocalDate.now().minusDays(2),
                        20.0,
                        16.0,
                        60.0,
                        "Dinner",
                        listOf()
                    )
                )
            ),
            DailyCalories(
                todayMeal = listOf(
                    FoodLog(
                        10,
                        userId,
                        330.0,
                        LocalDate.now().minusDays(1),
                        14.0,
                        9.0,
                        41.0,
                        "Breakfast",
                        listOf()
                    ),
                    FoodLog(
                        11,
                        userId,
                        640.0,
                        LocalDate.now().minusDays(1),
                        28.0,
                        22.0,
                        85.0,
                        "Lunch",
                        listOf()
                    ),
                    FoodLog(
                        12,
                        userId,
                        510.0,
                        LocalDate.now().minusDays(1),
                        23.0,
                        19.0,
                        70.0,
                        "Dinner",
                        listOf()
                    )
                )
            ),
            DailyCalories(
                todayMeal = listOf(
                    FoodLog(
                        13,
                        userId,
                        340.0,
                        LocalDate.now(),
                        13.0,
                        10.0,
                        43.0,
                        "Breakfast",
                        listOf()
                    ),
                    FoodLog(
                        14,
                        userId,
                        670.0,
                        LocalDate.now(),
                        32.0,
                        27.0,
                        95.0,
                        "Lunch",
                        listOf()
                    ),
                    FoodLog(
                        15,
                        userId,
                        490.0,
                        LocalDate.now(),
                        21.0,
                        17.0,
                        62.0,
                        "Dinner",
                        listOf()
                    )
                )
            )
        )
    }
}