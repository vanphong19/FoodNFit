package com.vanphong.foodnfit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.FoodLogDetail
import com.vanphong.foodnfit.R

class FoodLogDetailAdapter: RecyclerView.Adapter<FoodLogDetailAdapter.FoodLogDetailViewHolder>(){
    private var details: List<FoodLogDetail> = emptyList()
    init {
        setHasStableIds(true) // 👉 Bật chế độ sử dụng ID ổn định
    }
    fun submitList(newDetails: List<FoodLogDetail>){
        details = newDetails
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return details[position].id.toLong()
    }
    class FoodLogDetailViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val imgFoodMeal: ImageView = itemView.findViewById(R.id.img_food_meal)
        val tvFoodMeal: TextView = itemView.findViewById(R.id.tv_food_meal)
        val tvServingMeal: TextView = itemView.findViewById(R.id.tv_serving_meal)
        val btnX: ImageView = itemView.findViewById(R.id.btn_x)
        val tvFoodMealCalo: TextView = itemView.findViewById(R.id.tv_food_meal_calo)
        val tvFoodMealCarb: TextView = itemView.findViewById(R.id.tv_food_meal_carb)
        val tvFoodMealFat: TextView = itemView.findViewById(R.id.tv_food_meal_fat)
        val tvFoodMealProtein: TextView = itemView.findViewById(R.id.tv_food_meal_protein)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodLogDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_log_detail, parent, false)
        return FoodLogDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("FoodLogdetail", "Detail list size: ${details.size}")
        return details.size
    }

    override fun onBindViewHolder(holder: FoodLogDetailViewHolder, position: Int) {
        val detail = details[position]

        holder.imgFoodMeal.setImageResource(R.drawable.thit_bo)
        holder.tvFoodMeal.text = detail.foodName
        holder.tvServingMeal.text = detail.servingSize
        holder.tvFoodMealCalo.text = detail.calories.toString()
        holder.tvFoodMealCarb.text = detail.carb.toString()
        holder.tvFoodMealFat.text = detail.fat.toString()
        holder.tvFoodMealProtein.text = detail.protein.toString()
    }
}