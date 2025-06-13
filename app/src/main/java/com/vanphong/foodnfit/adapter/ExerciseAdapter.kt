package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.Model.WorkoutExercises
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.viewModel.ExerciseViewModel

class ExerciseAdapter(private val onClick: (exerciseId: Int) -> Unit,
                      private val onLongClick: (position: Int) -> Unit): ListAdapter<WorkoutExercises, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {
    class ExerciseViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val rlAddExercise: RelativeLayout = itemView.findViewById(R.id.rl_addExercise)
        val imgAddExercise: ImageView = itemView.findViewById(R.id.img_add_exercise)
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val lnMinuteKcal: LinearLayout = itemView.findViewById(R.id.ln_minute_kcal)
        val minutePractice: TextView = itemView.findViewById(R.id.minute_practice)
        val tv_kcalBurnt: TextView = itemView.findViewById(R.id.tv_kcalBurnt)
        val tvSets: TextView = itemView.findViewById(R.id.tvSets)
        val tvReps: TextView = itemView.findViewById(R.id.tvReps)
        val tvKcalBurnt: TextView = itemView.findViewById(R.id.tvkcalBurnt)
        val imgCheck: ImageView = itemView.findViewById(R.id.img_check)
        val lnSetRep: LinearLayout = itemView.findViewById(R.id.ln_set_rep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = getItem(position)

        holder.imgAddExercise.setImageResource(R.drawable.football)
        holder.tvExerciseName.text = exercise.name

        if (exercise.minute == null) {
            holder.lnMinuteKcal.visibility = View.GONE
            holder.lnSetRep.visibility = View.VISIBLE

            holder.tvReps.text = exercise.reps.toString()
            holder.tvSets.text = exercise.sets.toString()
            holder.tvKcalBurnt.text = exercise.caloriesBurnt.toString()
        } else {
            holder.lnSetRep.visibility = View.GONE
            holder.lnMinuteKcal.visibility = View.VISIBLE

            holder.minutePractice.text = exercise.minute.toString()
            holder.tv_kcalBurnt.text = exercise.caloriesBurnt.toString()
        }

        if (exercise.isCompleted) {
            holder.imgCheck.visibility = View.VISIBLE
            holder.rlAddExercise.setOnClickListener(null)
            holder.rlAddExercise.setOnLongClickListener(null)
            holder.rlAddExercise.isClickable = false
            holder.rlAddExercise.isFocusable = false
        } else {
            holder.imgCheck.visibility = View.GONE

            holder.rlAddExercise.setOnClickListener {
                onClick(exercise.exerciseId)
            }

            holder.rlAddExercise.setOnLongClickListener {
                onLongClick(holder.bindingAdapterPosition)
                true
            }

            holder.rlAddExercise.isClickable = true
            holder.rlAddExercise.isFocusable = true
        }

    }

}

class ExerciseDiffCallback: DiffUtil.ItemCallback<WorkoutExercises>(){
    override fun areItemsTheSame(oldItem: WorkoutExercises, newItem: WorkoutExercises): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WorkoutExercises, newItem: WorkoutExercises): Boolean {
        return oldItem == newItem
    }

}