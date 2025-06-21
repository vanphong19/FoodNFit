package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.model.Ingredient
import com.vanphong.foodnfit.R


class IngredientAdapter: ListAdapter<Ingredient, IngredientAdapter.IngredientViewModel>(IngredientDiffCallback()) {


    inner class IngredientViewModel(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val tvIngredient: TextView = itemView.findViewById(R.id.tv_ingredient)
        val tvIngredientSize: TextView = itemView.findViewById(R.id.tv_ingredient_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewModel {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_ingredient, parent, false)
        return IngredientViewModel(view)
    }

    override fun onBindViewHolder(holder: IngredientViewModel, position: Int) {
        val ingredient = getItem(position)

        holder.tvIngredient.text = ingredient.name
        holder.tvIngredientSize.text = ingredient.size
    }
}

class IngredientDiffCallback: DiffUtil.ItemCallback<Ingredient>(){
    override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem == newItem
    }

}