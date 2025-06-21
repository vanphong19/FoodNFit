package com.vanphong.foodnfit.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.activity.FeedbackActivity
import com.vanphong.foodnfit.activity.ProfileActivity
import com.vanphong.foodnfit.activity.SettingActivity
import com.vanphong.foodnfit.activity.SignInActivity
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
        _binding = FragmentPersonBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.personViewModel = viewModel

        observeNavigation()
        observeLogout()

        // Giả sử bạn có nút logout trong layout với id logoutButton
        binding.logoutButton.setOnClickListener {
            viewModel.onClickLogout(requireContext())
        }

        binding.layoutFeedback.setOnClickListener {
            val intent = Intent(requireContext(), FeedbackActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun observeNavigation() {
        viewModel.navigateProfile.observe(viewLifecycleOwner) { navigateProfile ->
            if (navigateProfile) {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
                viewModel.completeNavigateProfile()
            }
        }
        viewModel.navigateSetting.observe(viewLifecycleOwner) { navigateSetting ->
            if (navigateSetting) {
                val intent = Intent(requireContext(), SettingActivity::class.java)
                startActivity(intent)
                viewModel.completeNavigateSetting()
            }
        }
    }

    private fun observeLogout() {
        viewModel.logoutSuccess.observe(viewLifecycleOwner) { logoutSuccess ->
            if (logoutSuccess) {
                Toast.makeText(requireContext(), "Logout thành công", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                viewModel.completeLogout()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
