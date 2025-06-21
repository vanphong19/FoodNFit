package com.vanphong.foodnfit.admin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.model.FoodItemResponse
import com.vanphong.foodnfit.R

class FoodAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int, String) -> Unit
): ListAdapter<FoodItemResponse, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {
    class FoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgFood: ImageView = itemView.findViewById(R.id.imgFood)
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvCalo: TextView = itemView.findViewById(R.id.tvCalories)
        val tvServing: TextView = itemView.findViewById(R.id.tvServingSize)
        val tvMacros: TextView = itemView.findViewById(R.id.tvMacros)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_admin, parent, false)
        return FoodViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
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
        holder.tvCalo.text = "${food.calories} " + holder.itemView.context.getString(R.string.kcal)
        holder.tvServing.text = food.servingSizeEn
        holder.tvMacros.text = "P: ${food.protein}g | C: ${food.carbs}g | F: ${food.fat}g"
        // Click toàn bộ item (ngoại trừ nút)
        holder.itemView.setOnClickListener {
            onItemClick(food.id)
        }

        // Click nút Edit
        holder.btnEdit.setOnClickListener {
            onEditClick(food.id)
        }

        // Click nút Delete
        holder.btnDelete.setOnClickListener {
            onDeleteClick(food.id, food.nameEn)
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