package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.WorkoutPlan
import com.vanphong.foodnfit.R

class WorkoutLogAdapter: ListAdapter<WorkoutPlan, WorkoutLogAdapter.WorkoutLogViewHolder>(WorkoutLogDiffCallback()) {
    class WorkoutLogViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvWorkoutDate: TextView = itemView.findViewById(R.id.tvWorkoutDate)
        val tvExerciseCount: TextView = itemView.findViewById(R.id.tvExerciseCount)
        val tvExerciseBurnt: TextView = itemView.findViewById(R.id.tvExerciseBurnt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_log, parent, false)
        return WorkoutLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutLogViewHolder, position: Int) {
        val workout = getItem(position)

        holder.tvWorkoutDate.text = workout.date.toString()
        holder.tvExerciseCount.text = workout.exerciseCount.toString()
        holder.tvExerciseBurnt.text = workout.caloriesBurnt.toString()
    }
}
class WorkoutLogDiffCallback: DiffUtil.ItemCallback<WorkoutPlan>(){
    override fun areItemsTheSame(oldItem: WorkoutPlan, newItem: WorkoutPlan): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WorkoutPlan, newItem: WorkoutPlan): Boolean {
        return oldItem == newItem
    }
}