package com.vanphong.foodnfit.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.StatisticViewPagerAdapter
import com.vanphong.foodnfit.databinding.FragmentStatisticBinding
import com.vanphong.foodnfit.viewModel.StatisticViewModel


class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticViewModel by viewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var adapter: StatisticViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistic, container, false)

        binding.lifecycleOwner = this
        binding.statisticViewModel = viewModel

        viewPager = binding.viewPager
        toggleGroup = binding.toggleGroup

        adapter = StatisticViewPagerAdapter(this)
        viewPager.adapter = adapter

        setUpToggleButton()
        return binding.root
    }

    private fun setUpToggleButton(){
        toggleGroup.addOnButtonCheckedListener{ _, checkId, isChecked ->
            when(checkId){
                R.id.btn_calories ->{
                    if(isChecked){
                        viewPager.currentItem = 0
                        updateButtonStyles(R.id.btn_calories)
                    }
                }
                R.id.btn_weight -> {
                    if(isChecked){
                        viewPager.currentItem = 1
                        updateButtonStyles(R.id.btn_weight)
                    }
                }
                R.id.btn_exercise -> {
                    if(isChecked){
                        viewPager.currentItem = 2
                        updateButtonStyles(R.id.btn_exercise)
                    }
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> updateButtonStyles(R.id.btn_calories)
                    1 -> updateButtonStyles(R.id.btn_weight)
                    2 -> updateButtonStyles(R.id.btn_exercise)
                }
            }
        })
    }

    private fun updateButtonStyles(selectedButtonId: Int){
        for(i in 0 until toggleGroup.childCount){
            val button = toggleGroup.getChildAt(i) as MaterialButton
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        }

        val selectedButton = view?.findViewById<MaterialButton>(selectedButtonId)
        selectedButton?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.selected_color))
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}