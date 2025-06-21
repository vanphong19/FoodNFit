package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ItemFoodChooseBinding // Sử dụng ViewBinding
import com.vanphong.foodnfit.model.SelectedFoodItem

class ChooseFoodAdapter(private val onMinusClick: (SelectedFoodItem) -> Unit) :
    ListAdapter<SelectedFoodItem, ChooseFoodAdapter.ChooseFoodViewHolder>(ChooseFoodDiffCallback()) {

    // Sử dụng ViewBinding để code sạch hơn
    inner class ChooseFoodViewHolder(private val binding: ItemFoodChooseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(selectedFood: SelectedFoodItem) {
            binding.tvFoodName.text = selectedFood.foodItem.nameEn

            // Hiển thị khẩu phần và tổng calo đã được tính toán
            val servingCalo = "${selectedFood.totalServing} - ${"%.0f".format(selectedFood.totalCalories)} kcal"
            binding.tvServingCalo.text = servingCalo

            binding.btnMinusFood.setOnClickListener {
                onMinusClick(getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseFoodViewHolder {
        val binding = ItemFoodChooseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseFoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseFoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ChooseFoodDiffCallback : DiffUtil.ItemCallback<SelectedFoodItem>() {
    override fun areItemsTheSame(oldItem: SelectedFoodItem, newItem: SelectedFoodItem): Boolean {
        return oldItem.foodItem.id == newItem.foodItem.id
    }

    override fun areContentsTheSame(oldItem: SelectedFoodItem, newItem: SelectedFoodItem): Boolean {
        // So sánh cả quantity để RecyclerView biết khi nào cần vẽ lại item
        return oldItem.foodItem == newItem.foodItem && oldItem.quantity == newItem.quantity
    }
}