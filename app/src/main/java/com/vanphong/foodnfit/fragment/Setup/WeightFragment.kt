package com.vanphong.foodnfit.fragment.Setup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.FragmentWeightBinding

class WeightFragment : Fragment() {
    private var _binding: FragmentWeightBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeightBinding.inflate(layoutInflater)

        binding.horizontalPicker.setOnValueChangedListener { _, _, newVal ->
            binding.tvNumber.text = newVal.toString()
        }

        return binding.root
    }
}