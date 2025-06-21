package com.vanphong.foodnfit.admin.fragment

import android.app.AlertDialog
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
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.activity.AccountInfoActivity
import com.vanphong.foodnfit.admin.activity.AddEditFoodActivity
import com.vanphong.foodnfit.admin.activity.FoodDetailAdminActivity
import com.vanphong.foodnfit.admin.adapter.FoodAdapter
import com.vanphong.foodnfit.admin.viewModel.FoodViewModel
import com.vanphong.foodnfit.databinding.FragmentAdminFoodBinding
import com.vanphong.foodnfit.util.DialogUtils


class AdminFoodFragment : Fragment() {
    private lateinit var binding: FragmentAdminFoodBinding
    private lateinit var foodAdapter: FoodAdapter
    private val viewModel: FoodViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminFoodBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.foodViewModel = viewModel
        setRvFood()
        setScroll()

        binding.fabAddFood.setOnClickListener {
            val intent = Intent(requireContext(), AddEditFoodActivity::class.java)
            intent.putExtra("food_id", 0)
            intent.putExtra("title", getString(R.string.add_food))
            startActivity(intent)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(requireActivity(), getString(R.string.loading))
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }
        return binding.root
    }

    private fun setRvFood() {
        foodAdapter = FoodAdapter(
            onItemClick = { foodId ->
                val intent = Intent(requireContext(), FoodDetailAdminActivity::class.java)
                intent.putExtra("food_id", foodId)
                startActivity(intent)
            },
            onEditClick = {foodId ->
                val intent = Intent(requireContext(), AddEditFoodActivity::class.java)
                intent.putExtra("food_id", foodId)
                intent.putExtra("title", getString(R.string.edit_food))
                startActivity(intent)},
            onDeleteClick = {foodId, foodName ->
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirm_delete))
                    .setMessage(getString(R.string.delete_message, foodName))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        viewModel.removeFood(foodId)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
            }
        )
        viewModel.notify.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewFood.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.foodList.observe(viewLifecycleOwner){list->
            foodAdapter.submitList(list)
        }
        viewModel.loadMoreFoodItems()
        binding.recyclerViewFood.adapter = foodAdapter
    }

    private fun setScroll(){
        binding.recyclerViewFood.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Khi gần chạm đáy danh sách
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= 10 // pageSize
                ) {
                    viewModel.loadMoreFoodItems()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshList()
        setRvFood()
    }
}