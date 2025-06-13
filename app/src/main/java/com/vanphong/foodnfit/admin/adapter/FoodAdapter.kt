package com.vanphong.foodnfit.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.Model.FoodItemResponse
import com.vanphong.foodnfit.R

class FoodAdapter(private val onItemClick: (Int) -> Unit): ListAdapter<FoodItemResponse, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {
    class FoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgFood: ImageView = itemView.findViewById(R.id.imgFood)
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_admin, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = getItem(position)
        val imgUrl = food.imageUrl
        if(imgUrl.isNotEmpty()){
            Glide.with(holder.itemView.context)
                .load(imgUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.imgFood)
        }else{
            holder.imgFood.setImageResource(R.drawable.ic_placeholder)
        }
        holder.tvFoodName.text = food.nameEn

        holder.itemView.setOnClickListener {
            onItemClick(food.id)
        }
    }
}
class FoodDiffCallback: DiffUtil.ItemCallback<FoodItemResponse>() {
    override fun areItemsTheSame(oldItem: FoodItemResponse, newItem: FoodItemResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FoodItemResponse, newItem: FoodItemResponse): Boolean {
        return oldItem == newItem
    }
}