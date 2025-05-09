package com.vanphong.foodnfit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vanphong.foodnfit.Model.WorkoutExercises
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener

class SelectedExercisesAdapter(private val listener: OnExerciseAddListener):
    ListAdapter<WorkoutExercises, SelectedExercisesAdapter.SelectedExercisesViewHolder>(SelectedExercisesDiffCallback()) {
    class SelectedExercisesViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgExercise: ImageView = itemView.findViewById(R.id.img_add_exercise)
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val lnMinuteKcal: LinearLayout = itemView.findViewById(R.id.ln_minute_kcal)
        val minutePractice: TextView = itemView.findViewById(R.id.minute_practice)
        val tv_kcalBurnt: TextView = itemView.findViewById(R.id.tv_kcalBurnt)
        val tvSets: TextView = itemView.findViewById(R.id.tvSets)
        val tvReps: TextView = itemView.findViewById(R.id.tvReps)
        val tvKcalBurnt: TextView = itemView.findViewById(R.id.tvKcalBurnt)
        val btnMinusExercise: ImageView = itemView.findViewById(R.id.btn_minus_exercise)
        val lnSetRep: LinearLayout = itemView.findViewById(R.id.ln_set_rep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedExercisesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_choose_exercise, parent, false)
        return SelectedExercisesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedExercisesViewHolder, position: Int) {
        val selectedExercise = getItem(position)

        holder.imgExercise.setImageResource(R.drawable.football)
        holder.tvExerciseName.text = selectedExercise.name

        if (selectedExercise.minute == null) {
            holder.lnMinuteKcal.visibility = View.GONE
            holder.lnSetRep.visibility = View.VISIBLE

            holder.tvReps.text = selectedExercise.reps.toString()
            holder.tvSets.text = selectedExercise.sets.toString()
            holder.tvKcalBurnt.text = selectedExercise.caloriesBurnt.toString()
        } else {
            holder.lnSetRep.visibility = View.GONE
            holder.lnMinuteKcal.visibility = View.VISIBLE

            holder.minutePractice.text = selectedExercise.minute.toString()
            holder.tv_kcalBurnt.text = selectedExercise.caloriesBurnt.toString()
        }
        holder.btnMinusExercise.setOnClickListener {
            listener.onRemoveExercise(selectedExercise)
        }
    }
}

class SelectedExercisesDiffCallback: DiffUtil.ItemCallback<WorkoutExercises>() {
    override fun areItemsTheSame(oldItem: WorkoutExercises, newItem: WorkoutExercises): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WorkoutExercises, newItem: WorkoutExercises): Boolean {
        return oldItem == newItem
    }

}
