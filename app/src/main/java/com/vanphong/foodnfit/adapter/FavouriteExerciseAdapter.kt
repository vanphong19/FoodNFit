package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.FavoriteExerciseDto

class FavouriteExerciseAdapter: ListAdapter<FavoriteExerciseDto, FavouriteExerciseAdapter.FavouriteExerciseViewHolder>(FavouriteExerciseDiffCallback()) {
    class FavouriteExerciseViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val tvExerciseBurnt: TextView = itemView.findViewById(R.id.tvExerciseBurnt)
        val tvSession: TextView = itemView.findViewById(R.id.tvSession)
        val tvMuscle: TextView = itemView.findViewById(R.id.tvMuscle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_exercise, parent, false)
        return FavouriteExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteExerciseViewHolder, position: Int) {
        val exercise = getItem(position)
        holder.tvExerciseName.text = exercise.exerciseName
        holder.tvSession.text = exercise.totalSessions.toString()
        holder.tvMuscle.text = exercise.muscleGroup
        holder.tvExerciseBurnt.text = exercise.totalCalories.toString()
    }
}
class FavouriteExerciseDiffCallback: DiffUtil.ItemCallback<FavoriteExerciseDto>(){
    override fun areItemsTheSame(
        oldItem: FavoriteExerciseDto,
        newItem: FavoriteExerciseDto
    ): Boolean {
        return oldItem.exerciseName == newItem.exerciseName
    }

    override fun areContentsTheSame(
        oldItem: FavoriteExerciseDto,
        newItem: FavoriteExerciseDto
    ): Boolean {
        return oldItem == newItem
    }

}