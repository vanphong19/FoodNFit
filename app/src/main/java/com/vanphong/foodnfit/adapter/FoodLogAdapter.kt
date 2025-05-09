package com.vanphong.foodnfit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.FoodLog
import com.vanphong.foodnfit.R

class FoodLogAdapter: ListAdapter<FoodLog, FoodLogAdapter.FoodLogViewHolder>(FoodLogDiffCallback()) {
    class FoodLogViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        private val rvFoodLogDetail: RecyclerView = itemView.findViewById(R.id.rv_food_log_detail)
        val detailAdapter = FoodLogDetailAdapter()

        init {
            rvFoodLogDetail.adapter = detailAdapter
            rvFoodLogDetail.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL,false)
//            rvFoodLogDetail.setHasFixedSize(true)
//            rvFoodLogDetail.isNestedScrollingEnabled = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_log, parent, false)
        return FoodLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodLogViewHolder, position: Int) {
        val foodLog = getItem(position) ?: return

        val detail = foodLog.details
        Log.d("FoodLog", "Detail list size: ${detail.size}")
        holder.detailAdapter.submitList(detail)
    }
}
class FoodLogDiffCallback: DiffUtil.ItemCallback<FoodLog>(){
    override fun areItemsTheSame(oldItem: FoodLog, newItem: FoodLog): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FoodLog, newItem: FoodLog): Boolean {
        return oldItem == newItem
    }
}