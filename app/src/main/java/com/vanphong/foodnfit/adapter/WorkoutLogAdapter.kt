package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.model.WorkoutPlan
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.DailyCaloriesExerciseDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class WorkoutLogAdapter: ListAdapter<DailyCaloriesExerciseDto, WorkoutLogAdapter.WorkoutLogViewHolder>(WorkoutLogDiffCallback()) {
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

        val date = LocalDate.parse(workout.date)
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
        holder.tvWorkoutDate.text = date.format(formatter)

        holder.tvExerciseCount.text = workout.exerciseCount.toString()
        holder.tvExerciseBurnt.text = workout.totalCalories.roundToInt().toString()
    }
}
class WorkoutLogDiffCallback: DiffUtil.ItemCallback<DailyCaloriesExerciseDto>(){
    override fun areItemsTheSame(oldItem: DailyCaloriesExerciseDto, newItem: DailyCaloriesExerciseDto): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyCaloriesExerciseDto, newItem: DailyCaloriesExerciseDto): Boolean {
        return oldItem == newItem
    }
}