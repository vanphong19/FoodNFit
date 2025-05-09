package com.vanphong.foodnfit.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vanphong.foodnfit.fragment.statistic.CaloriesFragment
import com.vanphong.foodnfit.fragment.statistic.ExerciseFragment
import com.vanphong.foodnfit.fragment.statistic.WeightStatisticFragment

class StatisticViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CaloriesFragment()
            1 -> WeightStatisticFragment()
            else -> ExerciseFragment()
        }
    }
}