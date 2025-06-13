package com.vanphong.foodnfit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vanphong.foodnfit.fragment.Setup.BirthdayFragment
import com.vanphong.foodnfit.fragment.Setup.GenderFragment
import com.vanphong.foodnfit.fragment.Setup.GoalFragment
import com.vanphong.foodnfit.fragment.Setup.NameFragment
import com.vanphong.foodnfit.fragment.Setup.TallFragment
import com.vanphong.foodnfit.fragment.Setup.WeightFragment


class SetupViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> NameFragment()
            1 -> GenderFragment()
            2 -> BirthdayFragment()
            3 -> TallFragment()
            4 -> WeightFragment()
            else -> GoalFragment()
        }
    }
}