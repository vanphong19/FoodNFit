package com.vanphong.foodnfit.fragment.statistic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.Model.WorkoutPlan
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.WeightHistoryAdapter
import com.vanphong.foodnfit.adapter.WorkoutLogAdapter
import com.vanphong.foodnfit.databinding.FragmentExercise2Binding
import com.vanphong.foodnfit.databinding.FragmentWeightStatisticBinding
import com.vanphong.foodnfit.viewModel.ProfilesViewModel
import com.vanphong.foodnfit.viewModel.WorkoutPlanViewModel
import java.time.LocalDate
import java.util.UUID


class ExerciseFragment : Fragment() {
    private var _binding: FragmentExercise2Binding? = null
    private val binding get() = _binding!!
    private lateinit var workoutLogAdapter: WorkoutLogAdapter
    private val viewModel: WorkoutPlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExercise2Binding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.workoutViewModel = viewModel
        setDataRvWorkoutLog()
        return binding.root
    }

    private fun setDataRvWorkoutLog(){
        workoutLogAdapter = WorkoutLogAdapter()
        binding.rvWorkoutLog.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.workoutList.observe(requireActivity()){list ->
            workoutLogAdapter.submitList(list)
        }
        viewModel.setWorkoutList(getData())
        binding.rvWorkoutLog.adapter = workoutLogAdapter
    }

    private fun getData(): List<WorkoutPlan> {
        return listOf(
            WorkoutPlan(
                id = 1,
                userId = UUID.fromString("a1b2c3d4-e5f6-7890-1234-abcdefabcdef"),
                exerciseCount = 3,
                date = LocalDate.of(2025, 5, 15),
                caloriesBurnt = 420.5f
            ),
            WorkoutPlan(
                id = 2,
                userId = UUID.fromString("a1b2c3d4-e5f6-7890-1234-abcdefabcdef"),
                exerciseCount = 2,
                date = LocalDate.of(2025, 5, 16),
                caloriesBurnt = 350.0f
            ),
            WorkoutPlan(
                id = 3,
                userId = UUID.fromString("a1b2c3d4-e5f6-7890-1234-abcdefabcdef"),
                exerciseCount = 4,
                date = LocalDate.of(2025, 5, 17),
                caloriesBurnt = 510.75f
            ),
            WorkoutPlan(
                id = 4,
                userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                exerciseCount = 1,
                date = LocalDate.of(2025, 5, 17),
                caloriesBurnt = 120.0f
            )
        )
    }
}