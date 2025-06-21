package com.vanphong.foodnfit.admin.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import java.math.BigDecimal
import java.math.RoundingMode

class ExerciseListAdapter(
    private val onItemClick: (Int) -> Unit
): ListAdapter<Exercises, ExerciseListAdapter.ExerciseListViewHolder>(ExerciseListDiffCallback()) {
    class ExerciseListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rlAddExercise: RelativeLayout = itemView.findViewById(R.id.rl_addExercise)
        val imgExercise: ImageView = itemView.findViewById(R.id.img_add_exercise)
        val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val lnMinuteKcal: LinearLayout = itemView.findViewById(R.id.ln_minute_kcal)
        val minutePractice: TextView = itemView.findViewById(R.id.minute_practice)
        val tv_kcalBurnt: TextView = itemView.findViewById(R.id.tv_kcalBurnt)
        val tvSets: TextView = itemView.findViewById(R.id.tvSets)
        val tvReps: TextView = itemView.findViewById(R.id.tvReps)
        val tvKcalBurnt: TextView = itemView.findViewById(R.id.tvKcalBurnt)
        val lnSetRep: LinearLayout = itemView.findViewById(R.id.ln_set_rep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_exercise, parent, false)
        return ExerciseListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseListViewHolder, position: Int) {
        val exercise = getItem(position)

        Glide.with(holder.itemView.context)
            .load(exercise.imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(holder.imgExercise)

        holder.tvExerciseName.text = exercise.name

        if (exercise.minutes == 0) {
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

        holder.itemView.setOnClickListener {
            onItemClick(exercise.id)
        }
    }

//    private fun showMinuteDialog(context: Context, exercise: Exercises) {
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_choose_time_dialog, null)
//
//        val dialog = AlertDialog.Builder(context)
//            .setView(dialogView)
//            .setCancelable(false)
//            .create()
//
//        // Yêu cầu không tiêu đề
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        // Cấu hình kích thước và vị trí cửa sổ
//        dialog.setOnShowListener {
//            val window = dialog.window ?: return@setOnShowListener
//            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
//            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            val windowAttributes = window.attributes
//            windowAttributes.gravity = Gravity.BOTTOM
//            window.attributes = windowAttributes
//        }
//
//        // Ánh xạ view trong dialog
//        val tvExerciseName = dialogView.findViewById<TextView>(R.id.tvExerciseName)
//        val minutePractice = dialogView.findViewById<TextView>(R.id.minute_practice)
//        val tvKcalBurnt = dialogView.findViewById<TextView>(R.id.tv_kcalBurnt)
//        val npHour = dialogView.findViewById<NumberPicker>(R.id.npHour)
//        val npMinute = dialogView.findViewById<NumberPicker>(R.id.npMinute)
//        val btnAdd = dialogView.findViewById<Button>(R.id.btnAddActivity)
//        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
//
//        // Gán dữ liệu
//        tvExerciseName.text = exercise.name
//        npHour.minValue = 0
//        npHour.maxValue = 5
//        npMinute.minValue = 0
//        npMinute.maxValue = 59
//
//        fun updateCalculations() {
//            val totalMinutes = npHour.value * 60 + npMinute.value
//            // Giả sử rằng exercise.minutes luôn có giá trị (dùng !! nếu chắc chắn không null)
//            val rawCaloriesBurnt = (exercise.caloriesBurnt / (exercise.minutes ?: 1)) * totalMinutes
//            val caloriesBurnt = BigDecimal(rawCaloriesBurnt.toDouble())
//                .setScale(2, RoundingMode.HALF_UP)
//                .toFloat()
//
//            minutePractice.text = totalMinutes.toString()
//            tvKcalBurnt.text = caloriesBurnt.toString()
//        }
//
//        // Cập nhật ban đầu
//        updateCalculations()
//
//        // Lắng nghe sự thay đổi của npHour và npMinute
//        npHour.setOnValueChangedListener { _, _, _ ->
//            updateCalculations()
//        }
//
//        npMinute.setOnValueChangedListener { _, _, _ ->
//            updateCalculations()
//        }
//
//        btnCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        btnAdd.setOnClickListener {
//            val totalMinutes = npHour.value * 60 + npMinute.value
//            listener.onAddExerciseByMinute(exercise, totalMinutes)  // Gọi callback
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//
//    private fun showSetRepDialog(context: Context, exercise: Exercises){
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_set_rep_dialog, null)
//
//        val dialog = AlertDialog.Builder(context)
//            .setView(dialogView)
//            .setCancelable(false)
//            .create()
//
//        // Yêu cầu không tiêu đề
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        // Cấu hình kích thước và vị trí cửa sổ
//        dialog.setOnShowListener {
//            val window = dialog.window ?: return@setOnShowListener
//            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
//            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            val windowAttributes = window.attributes
//            windowAttributes.gravity = Gravity.BOTTOM
//            window.attributes = windowAttributes
//        }
//
//        // Ánh xạ view trong dialog
//        val tvExerciseName = dialogView.findViewById<TextView>(R.id.tvExerciseName)
//        val tvSet = dialogView.findViewById<TextView>(R.id.tvSets)
//        val tvRep = dialogView.findViewById<TextView>(R.id.tvReps)
//        val tvCLBurnt = dialogView.findViewById<TextView>(R.id.tvKcalBurnt)
//        val npSet = dialogView.findViewById<NumberPicker>(R.id.npSet)
//        val npRep = dialogView.findViewById<NumberPicker>(R.id.npRep)
//        val btnAdd = dialogView.findViewById<Button>(R.id.btnAddActivity)
//        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
//
//        // Gán dữ liệu
//        tvExerciseName.text = exercise.name
//        npSet.minValue = 1
//        npSet.maxValue = 60
//        npRep.minValue = 1
//        npRep.maxValue = 60
//
//        fun updateCalculations() {
//            tvSet.text = npSet.value.toString()
//            tvRep.text = npRep.value.toString()
//        }
//
//        // Cập nhật ban đầu
//        updateCalculations()
//
//        // Lắng nghe sự thay đổi của npHour và npMinute
//        npSet.setOnValueChangedListener { _, _, _ ->
//            updateCalculations()
//        }
//
//        npRep.setOnValueChangedListener { _, _, _ ->
//            updateCalculations()
//        }
//
//        btnCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        btnAdd.setOnClickListener {
//            val set = npSet.value
//            val rep = npRep.value
//            listener.onAddExerciseBySetRep(exercise, set, rep)  // Gọi callback
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
}
class ExerciseListDiffCallback: DiffUtil.ItemCallback<Exercises>(){
    override fun areItemsTheSame(oldItem: Exercises, newItem: Exercises): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Exercises, newItem: Exercises): Boolean {
        return oldItem == newItem
    }

}
