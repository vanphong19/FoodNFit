package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.model.FoodItem
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.FoodItemResponse
import de.hdodenhof.circleimageview.CircleImageView

class FoodAdapter(private var isGridView: Boolean, private val onAddFoodClick: (FoodItemResponse) -> Unit,
                  private val onItemClick: (FoodItemResponse) -> Unit) :
    ListAdapter<FoodItemResponse, RecyclerView.ViewHolder>(FoodDiffCallback()) {

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
        val btnAddFood: CircleImageView = itemView.findViewById(R.id.btn_add)
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
        // Dùng %.0f để làm tròn calo thành số nguyên khi hiển thị
        val recipeCalo = "${food.servingSizeEn} - ${"%.0f".format(food.calories)} kcal"

        holder.itemView.setOnClickListener {
            onItemClick(food)
        }

        when (holder) {
            is FoodGridViewHolder -> {
                // Sử dụng thuộc tính từ FoodItemResponse
                holder.tvFoodName.text = food.nameEn
                holder.tvSizeCalo.text = recipeCalo
                Glide.with(holder.itemView.context)
                    .load(food.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.imgFood)
                holder.btnAddFood.setOnClickListener {
                    onAddFoodClick(food)
                }
            }
            is FoodListViewHolder -> {
                holder.tvFoodName.text = food.nameEn
                holder.tvSizeCalo.text = recipeCalo
                holder.btnAddFood.setOnClickListener{
                    onAddFoodClick(food)
                }
            }
        }
    }

    fun setLayoutType(isGrid: Boolean) {
        this.isGridView = isGrid
        notifyDataSetChanged() // bắt buộc reload lại layout
    }
}
class FoodDiffCallback: DiffUtil.ItemCallback<FoodItemResponse>(){
    override fun areItemsTheSame(oldItem: FoodItemResponse, newItem: FoodItemResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FoodItemResponse, newItem: FoodItemResponse): Boolean {
        return oldItem == newItem
    }

}