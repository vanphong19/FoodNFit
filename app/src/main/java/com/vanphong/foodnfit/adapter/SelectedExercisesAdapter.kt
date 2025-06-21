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
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import com.vanphong.foodnfit.interfaces.OnExerciseListener
import com.vanphong.foodnfit.model.WorkoutExercises

class SelectedExercisesAdapter(private val listener: OnExerciseListener) :
    ListAdapter<WorkoutExercises, SelectedExercisesAdapter.SelectedExercisesViewHolder>(SelectedExercisesDiffCallback()) {

    // Đảm bảo ViewHolder này ánh xạ đúng đến các ID trong item_choose_exercise.xml
    class SelectedExercisesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Giả sử các ID này tồn tại trong R.layout.item_choose_exercise
        val imgExercise: ImageView = itemView.findViewById(R.id.img_add_exercise)
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)

        // Layout cho loại minute/kcal
        val lnMinuteKcal: LinearLayout = itemView.findViewById(R.id.ln_minute_kcal)
        val minutePractice: TextView = itemView.findViewById(R.id.minute_practice)
        val tv_kcalBurnt: TextView = itemView.findViewById(R.id.tv_kcalBurnt)

        // Layout cho loại set/rep
        val lnSetRep: LinearLayout = itemView.findViewById(R.id.ln_set_rep)
        val tvSets: TextView = itemView.findViewById(R.id.tvSets)
        val tvReps: TextView = itemView.findViewById(R.id.tvReps)
        val tvKcalBurnt: TextView = itemView.findViewById(R.id.tvKcalBurnt) // ID này có thể trùng lặp, cần cẩn thận

        val btnMinusExercise: ImageView = itemView.findViewById(R.id.btn_minus_exercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedExercisesViewHolder {
        // Đảm bảo bạn đang inflate đúng layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_choose_exercise, parent, false)
        return SelectedExercisesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedExercisesViewHolder, position: Int) {
        val selectedExercise = getItem(position)

        holder.imgExercise.setImageResource(R.drawable.football)
        holder.tvExerciseName.text = selectedExercise.name

        // LOGIC HIỂN THỊ CHÍNH XÁC
        if (selectedExercise.minute == null) {
            // Đây là bài tập set/rep
            holder.lnMinuteKcal.visibility = View.GONE
            holder.lnSetRep.visibility = View.VISIBLE

            // Hiển thị giá trị từ workoutExercise
            holder.tvSets.text = selectedExercise.sets.toString()
            holder.tvReps.text = selectedExercise.reps.toString()
            // Đảm bảo bạn đang gán giá trị cho đúng TextView
            // Nếu bạn có 2 TextView với id `tvKcalBurnt`, hãy đổi tên một trong hai để phân biệt.
            holder.tvKcalBurnt.text = selectedExercise.caloriesBurnt.toString()
        } else {
            // Đây là bài tập tính theo phút
            holder.lnSetRep.visibility = View.GONE
            holder.lnMinuteKcal.visibility = View.VISIBLE

            // Hiển thị giá trị từ workoutExercise
            holder.minutePractice.text = selectedExercise.minute.toString()
            holder.tv_kcalBurnt.text = selectedExercise.caloriesBurnt.toString()
        }

        holder.btnMinusExercise.setOnClickListener {
            listener.onExerciseRemoved(selectedExercise)
        }
    }
}

// DiffCallback không thay đổi
class SelectedExercisesDiffCallback : DiffUtil.ItemCallback<WorkoutExercises>() {
    override fun areItemsTheSame(oldItem: WorkoutExercises, newItem: WorkoutExercises): Boolean {
        return oldItem.exerciseId == newItem.exerciseId // So sánh bằng exerciseId ổn định hơn
    }

    override fun areContentsTheSame(oldItem: WorkoutExercises, newItem: WorkoutExercises): Boolean {
        return oldItem == newItem
    }
}