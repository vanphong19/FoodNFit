package com.vanphong.foodnfit.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.activity.SettingActivity
import com.vanphong.foodnfit.databinding.FragmentPersonBinding
import com.vanphong.foodnfit.viewModel.PersonViewModel

class PersonFragment : Fragment() {
    private var _binding: FragmentPersonBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.personViewModel = viewModel
        navigateSetting()
        return binding.root
    }

    private fun navigateSetting(){
        viewModel.navigateSetting.observe(requireActivity(), Observer { navigateSetting ->
            if(navigateSetting)
            {
                val intent = Intent(requireContext(), SettingActivity::class.java)
                startActivity(intent)
            }
        })
    }
}