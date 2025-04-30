package com.vanphong.foodnfit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.DataBindingUtil
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.FragmentHomeBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.tvDate.text = getCurrentVietnameseDate()
        updateIndicator()
        return binding.root
    }

    private fun updateIndicator() {
        val minValue = binding.bmiProgressbar.min.toFloat()
        val maxValue = binding.bmiProgressbar.max.toFloat()
        val currentValue = binding.tvBmi.text.toString().toFloatOrNull()
        val percent = if (currentValue != null && maxValue > minValue) {
            ((currentValue - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
        } else {
            0f
        }

        binding.indicatorGuideline.setGuidelinePercent(percent)
    }

    private fun getCurrentVietnameseDate(): String{
        val currentDate = LocalDate.now()
        val vietnameseLocale = Locale("vi","VN")
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'tháng' M", vietnameseLocale)
        return currentDate.format(formatter)
    }

    override fun onResume() {
        binding.tvDate.text = getCurrentVietnameseDate()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}