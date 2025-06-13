package com.vanphong.foodnfit.fragment.statistic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.vanphong.foodnfit.Model.UserProfiles
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.WeightHistoryAdapter
import com.vanphong.foodnfit.databinding.FragmentWeightStatisticBinding
import com.vanphong.foodnfit.viewModel.ProfilesViewModel
import java.time.LocalDate


class WeightStatisticFragment : Fragment() {
    private var _binding: FragmentWeightStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var weightHistoryAdapter: WeightHistoryAdapter
    private val viewModel: ProfilesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeightStatisticBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.profilesViewModel = viewModel
        initRvWeightHistory()
        return binding.root
    }

    private fun initRvWeightHistory(){
        weightHistoryAdapter = WeightHistoryAdapter()
        binding.rvWeightHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.weightHistories.observe(requireActivity()){list ->
            weightHistoryAdapter.submitList(list)
        }
        viewModel.setWeightHistories(getData())
        binding.rvWeightHistory.adapter = weightHistoryAdapter
    }

    private fun getData(): List<UserProfiles> {
        return listOf(
            UserProfiles(
                id = "1",
                userId = "user_001",
                height = 170f,
                weight = 65f,
                tdee = 2200f,
                goal = "maintain",
                date = LocalDate.of(2025, 5, 1)
            ),
            UserProfiles(
                id = "2",
                userId = "user_002",
                height = 160f,
                weight = 50f,
                tdee = 1800f,
                goal = "gain",
                date = LocalDate.of(2025, 5, 2)
            ),
            UserProfiles(
                id = "3",
                userId = "user_003",
                height = 180f,
                weight = 85f,
                tdee = 2500f,
                goal = "lose",
                date = LocalDate.of(2025, 5, 3)
            ),
            UserProfiles(
                id = "4",
                userId = "user_004",
                height = 175f,
                weight = 70f,
                tdee = 2300f,
                goal = "maintain",
                date = LocalDate.of(2025, 5, 4)
            ),
            UserProfiles(
                id = "5",
                userId = "user_005",
                height = 165f,
                weight = 55f,
                tdee = 2000f,
                goal = "gain",
                date = LocalDate.of(2025, 5, 5)
            )
        )
    }
}