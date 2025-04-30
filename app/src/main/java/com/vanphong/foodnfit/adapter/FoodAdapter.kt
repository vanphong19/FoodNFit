package com.vanphong.foodnfit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.R
import de.hdodenhof.circleimageview.CircleImageView

class FoodAdapter(private var isGridView: Boolean) :
    ListAdapter<FoodItem, RecyclerView.ViewHolder>(FoodDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_GRID = 0
        private const val VIEW_TYPE_LIST = 1
    }

    // Grid ViewHolder
    class FoodGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFood: ImageView = itemView.findViewById(R.id.img_food)
        val tvFoodName: TextView = itemView.findViewById(R.id.tv_food_name)
        val tvSizeCalo: TextView = itemView.findViewById(R.id.tv_recipe_calo)
        val lnMoreDetail: ImageView = itemView.findViewById(R.id.btn_more_detail)
        val tvAddFood: CircleImageView = itemView.findViewById(R.id.btn_add)
    }

    // List/Vertical ViewHolder
    class FoodListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoodName: TextView = itemView.findViewById(R.id.tv_food_name)
        val tvSizeCalo: TextView = itemView.findViewById(R.id.tv_recipe_calo)
        val btnAddFood: ImageView = itemView.findViewById(R.id.btn_add_food)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridView) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val view = inflater.inflate(R.layout.item_food, parent, false)
                FoodGridViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_food_vertical, parent, false)
                FoodListViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val food = getItem(position)
        val recipeCalo = "${food.servingSize} - ${food.calories} kcal"

        when (holder) {
            is FoodGridViewHolder -> {
                holder.tvFoodName.text = food.name
                holder.tvSizeCalo.text = recipeCalo
                holder.imgFood.setImageResource(R.drawable.thit_bo) // TODO: load image from URL later
            }
            is FoodListViewHolder -> {
                holder.tvFoodName.text = food.name
                holder.tvSizeCalo.text = recipeCalo
            }
        }
    }

    fun setLayoutType(isGrid: Boolean) {
        this.isGridView = isGrid
        notifyDataSetChanged() // bắt buộc reload lại layout
    }
}
class FoodDiffCallback: DiffUtil.ItemCallback<FoodItem>(){
    override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
        return oldItem == newItem
    }

}