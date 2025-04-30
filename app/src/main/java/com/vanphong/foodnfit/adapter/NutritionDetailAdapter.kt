package com.vanphong.foodnfit.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R

class NutritionDetailAdapter: RecyclerView.Adapter<NutritionDetailAdapter.NutritionDetailViewHolder>(){

    class NutritionDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgNutrition: ImageView = itemView.findViewById(R.id.img_nutrition)
        val tvNutritionName: TextView = itemView.findViewById(R.id.tv_nutrition_name)
        val tvNutritionDetail: TextView = itemView.findViewById(R.id.tv_nutrition_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionDetailViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: NutritionDetailViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}