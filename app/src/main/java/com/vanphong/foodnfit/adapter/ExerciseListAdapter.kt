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
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import com.vanphong.foodnfit.interfaces.OnExerciseListener
import com.vanphong.foodnfit.model.SelectableExercise

class ExerciseListAdapter(private val listener: OnExerciseListener) :
    ListAdapter<SelectableExercise, ExerciseListAdapter.ExerciseListViewHolder>(SelectableExerciseDiffCallback()) {

    class ExerciseListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rootLayout: RelativeLayout = itemView.findViewById(R.id.rl_addExercise) // Tham chiếu đến layout gốc
        val imgExercise: ImageView = itemView.findViewById(R.id.img_add_exercise)
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val lnMinuteKcal: LinearLayout = itemView.findViewById(R.id.ln_minute_kcal)
        val minutePractice: TextView = itemView.findViewById(R.id.minute_practice)
        val tv_kcalBurnt: TextView = itemView.findViewById(R.id.tv_kcalBurnt)
        val tvSets: TextView = itemView.findViewById(R.id.tvSets)
        val tvReps: TextView = itemView.findViewById(R.id.tvReps)
        val tvKcalBurnt: TextView = itemView.findViewById(R.id.tvKcalBurnt)
        val btnAddExercise: ImageView = itemView.findViewById(R.id.btn_add_exercise)
        val lnSetRep: LinearLayout = itemView.findViewById(R.id.ln_set_rep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseListViewHolder, position: Int) {
        val selectableExercise = getItem(position)
        val exercise = selectableExercise.exercise

        // Bind dữ liệu như cũ
        holder.imgExercise.setImageResource(R.drawable.football)
        holder.tvExerciseName.text = exercise.name

        if (exercise.minutes == null) {
            holder.lnMinuteKcal.visibility = View.GONE
            holder.lnSetRep.visibility = View.VISIBLE
            holder.tvReps.text = exercise.reps.toString()
            holder.tvSets.text = exercise.sets.toString()
            holder.tvKcalBurnt.text = exercise.caloriesBurnt.toString()
        } else {
            holder.lnSetRep.visibility = View.GONE
            holder.lnMinuteKcal.visibility = View.VISIBLE
            holder.minutePractice.text = exercise.minutes.toString()
            holder.tv_kcalBurnt.text = exercise.caloriesBurnt.toString()
        }

        // Cập nhật trạng thái của item
        if (selectableExercise.isSelected) {
            holder.btnAddExercise.setImageResource(R.drawable.ic_check)
            holder.rootLayout.isEnabled = false // Vô hiệu hóa click trên toàn bộ item
            holder.rootLayout.alpha = 0.6f // Làm mờ item để cho biết đã được chọn
        } else {
            holder.btnAddExercise.setImageResource(R.drawable.ic_plus_gray)
            holder.rootLayout.isEnabled = true
            holder.rootLayout.alpha = 1.0f
        }

        // Gán listener cho toàn bộ item view
        holder.rootLayout.setOnClickListener {
            // Chỉ gọi listener nếu item chưa được chọn
            if (!selectableExercise.isSelected) {
                listener.onExerciseAdded(exercise)
            }
        }
    }
    // Không cần các hàm show...Dialog nữa!
}

// DiffCallback không thay đổi
class SelectableExerciseDiffCallback : DiffUtil.ItemCallback<SelectableExercise>() {
    override fun areItemsTheSame(oldItem: SelectableExercise, newItem: SelectableExercise): Boolean {
        return oldItem.exercise.id == newItem.exercise.id
    }
    override fun areContentsTheSame(oldItem: SelectableExercise, newItem: SelectableExercise): Boolean {
        return oldItem == newItem
    }
}