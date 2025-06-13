package com.vanphong.foodnfit.admin.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.vanphong.foodnfit.Model.User
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.activity.AccountActivity
import com.vanphong.foodnfit.admin.activity.AccountInfoActivity
import com.vanphong.foodnfit.admin.adapter.AccountAdapter
import com.vanphong.foodnfit.admin.viewModel.AccountViewModel
import com.vanphong.foodnfit.databinding.FragmentAdminAccountBinding

class AdminAccountFragment : Fragment() {
    private lateinit var binding: FragmentAdminAccountBinding
    private lateinit var accountAdapter: AccountAdapter
    private val viewModel: AccountViewModel by viewModels()
    private lateinit var chipGroupGender: ChipGroup
    private lateinit var chipGroupStatus: ChipGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminAccountBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.accountViewModel = viewModel
        chipGroupGender = binding.chipGroupGender
        chipGroupStatus = binding.chipGroupStatus

        setRvAccount()
        create()
        setChipGenderInit()
        setChipStatus()

        binding.btnClearFilter.setOnClickListener {
            binding.btnFilterStatus.text = getString(R.string.button_filter_status)
            binding.btnFilterGender.text = getString(R.string.button_filter_gender)
            viewModel.setGender(null)
            viewModel.setBlocked(null)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshList()
            binding.swipeRefresh.isRefreshing = false
        }
        return binding.root
    }
    private fun setRvAccount() {
        accountAdapter = AccountAdapter{userId ->
            val intent = Intent(requireContext(), AccountInfoActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        binding.rvAccounts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.accountList.observe(viewLifecycleOwner) {
            accountAdapter.submitList(it)
        }

        // Add scroll listener
        binding.rvAccounts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= viewModel.pageSize
                ) {
                    viewModel.loadNextPage()
                }
            }
        })

        // Load trang đầu tiên
        viewModel.loadNextPage()
        binding.rvAccounts.adapter = accountAdapter
    }

    private fun create(){
        viewModel.navigateCreate.observe(requireActivity()){navigateCreate ->
            if(navigateCreate){
                val intent = Intent(requireContext(), AccountActivity::class.java)
                startActivity(intent)
                viewModel.completeCreate()
            }
        }
    }

    private fun setChipGenderInit() {
        val btn = binding.btnFilterGender
        val chipGroup = binding.chipGroupGender

        btn.setOnClickListener {
            chipGroup.visibility = if (chipGroup.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChipId = checkedIds[0]
                val selectedChip = group.findViewById<Chip>(selectedChipId)
                val selectedText = selectedChip.text.toString()
                var gender: Boolean? = null

                // Đổi text nút thành chip được chọn
                btn.text = selectedText

                // Ẩn chipGroup sau khi chọn
                chipGroup.visibility = View.GONE

                if(selectedText == getString(R.string.male)){
                    gender = false
                }
                else if(selectedText == getString(R.string.female)){
                    gender = true
                }
                else gender = null

                viewModel.setGender(gender)
                viewModel.refreshList()
            }
        }
    }
    private fun setChipStatus(){
        val btn = binding.btnFilterStatus
        val chipGroup = binding.chipGroupStatus

        btn.setOnClickListener {
            chipGroup.visibility = if (chipGroup.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChipId = checkedIds[0]
                val selectedChip = group.findViewById<Chip>(selectedChipId)
                val selectedText = selectedChip.text.toString()
                var status: Boolean? = null

                // Đổi text nút thành chip được chọn
                btn.text = selectedText

                // Ẩn chipGroup sau khi chọn
                chipGroup.visibility = View.GONE

                status = if(selectedText == getString(R.string.blocked)){
                    true
                } else if(selectedText == getString(R.string.un_blocked)){
                    false
                } else null

                viewModel.setBlocked(status)
                viewModel.refreshList()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setRvAccount()
    }
}