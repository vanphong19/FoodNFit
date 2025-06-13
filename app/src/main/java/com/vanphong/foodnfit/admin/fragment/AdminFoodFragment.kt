package com.vanphong.foodnfit.admin.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.activity.AccountInfoActivity
import com.vanphong.foodnfit.admin.activity.FoodDetailAdminActivity
import com.vanphong.foodnfit.admin.adapter.FoodAdapter
import com.vanphong.foodnfit.admin.viewModel.FoodViewModel
import com.vanphong.foodnfit.databinding.FragmentAdminFoodBinding


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
        return binding.root
    }

    private fun setRvFood() {
        foodAdapter = FoodAdapter{foodId ->
            val intent = Intent(requireContext(), FoodDetailAdminActivity::class.java)
            intent.putExtra("food_id", foodId)
            startActivity(intent)
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
}