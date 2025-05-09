package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.Model.SelectedFoodItem
import com.vanphong.foodnfit.R

class ChooseFoodAdapter(private val onMinusClick: (SelectedFoodItem) -> Unit): ListAdapter<SelectedFoodItem, ChooseFoodAdapter.ChooseFoodViewHolder>(ChooseFoodDiffCallback()) {
    class ChooseFoodViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val tvFoodName: TextView = itemView.findViewById(R.id.tv_food_name)
        val tvServingCalo: TextView = itemView.findViewById(R.id.tv_serving_calo)
        val btnMinusFood: ImageView = itemView.findViewById(R.id.btn_minus_food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_choose, parent, false)
        return ChooseFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseFoodViewHolder, position: Int) {
        val food = getItem(position)

        holder.tvFoodName.text = food.foodItem.name
        val servingCalo = "${food.totalServing} - ${food.totalCalories} kcal"
        holder.tvServingCalo.text = servingCalo
        holder.btnMinusFood.setOnClickListener{
            onMinusClick(food)
        }
    }
}
class ChooseFoodDiffCallback : DiffUtil.ItemCallback<SelectedFoodItem>() {
    override fun areItemsTheSame(oldItem: SelectedFoodItem, newItem: SelectedFoodItem): Boolean {
        return oldItem.foodItem.id == newItem.foodItem.id
    }

    override fun areContentsTheSame(oldItem: SelectedFoodItem, newItem: SelectedFoodItem): Boolean {
        return oldItem.foodItem == newItem.foodItem && oldItem.quantity == newItem.quantity
    }
}