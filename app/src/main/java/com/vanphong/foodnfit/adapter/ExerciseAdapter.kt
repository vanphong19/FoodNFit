package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ItemAddExerciseBinding
import com.vanphong.foodnfit.model.WorkoutExerciseResponse

class ExerciseAdapter(
    private val onClick: (exerciseId: Int) -> Unit,
    private val onLongClick: (position: Int) -> Unit
) : ListAdapter<WorkoutExerciseResponse, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    inner class ExerciseViewHolder(val binding: ItemAddExerciseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: WorkoutExerciseResponse) {
            binding.imgAddExercise.setImageResource(R.drawable.football) // Thay bằng ảnh thật nếu có
            binding.tvExerciseName.text = exercise.exerciseName

            // Giả sử backend đã sửa và trả về reps/sets là String
            if (exercise.minutes == 0) {
                // Loại theo Set/Rep
                binding.lnMinuteKcal.visibility = View.GONE
                binding.lnSetRep.visibility = View.VISIBLE

                // Sửa lại: Không cần .toString() vì đã là String
                binding.tvReps.text = exercise.reps.toString()
                binding.tvSets.text = exercise.sets.toString()
                binding.tvkcalBurnt.text = exercise.caloriesBurnt.toString()
            } else {
                // Loại theo thời gian
                binding.lnSetRep.visibility = View.GONE
                binding.lnMinuteKcal.visibility = View.VISIBLE

                binding.minutePractice.text = exercise.minutes.toString()
                binding.tvKcalBurnt.text = exercise.caloriesBurnt.toString()
            }

            // Xử lý listener
            if (exercise.isCompleted) {
                binding.imgCheck.visibility = View.VISIBLE
                binding.rlAddExercise.setOnLongClickListener(null)
                binding.rlAddExercise.isClickable = false
            } else {
                binding.imgCheck.visibility = View.GONE
                binding.rlAddExercise.setOnLongClickListener {
                    onLongClick(bindingAdapterPosition)
                    true
                }
                binding.rlAddExercise.isClickable = true
            }
            binding.rlAddExercise.setOnClickListener {
                onClick(exercise.exerciseId ?: return@setOnClickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemAddExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = getItem(position)
        holder.bind(exercise)
    }
}

class ExerciseDiffCallback : DiffUtil.ItemCallback<WorkoutExerciseResponse>() {
    override fun areItemsTheSame(oldItem: WorkoutExerciseResponse, newItem: WorkoutExerciseResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WorkoutExerciseResponse, newItem: WorkoutExerciseResponse): Boolean {
        return oldItem == newItem
    }
}