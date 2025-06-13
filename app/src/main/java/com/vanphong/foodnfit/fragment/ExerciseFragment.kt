package com.vanphong.foodnfit.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.Model.WorkoutExercises
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.activity.ExerciseInformationActivity
import com.vanphong.foodnfit.activity.ExerciseListActivity
import com.vanphong.foodnfit.adapter.CalendarAdapter
import com.vanphong.foodnfit.adapter.ExerciseAdapter
import com.vanphong.foodnfit.databinding.FragmentExerciseBinding
import com.vanphong.foodnfit.util.CalendarUtils
import com.vanphong.foodnfit.viewModel.ExerciseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class ExerciseFragment : Fragment(), CalendarAdapter.OnItemListener {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var exerciseAdapter: ExerciseAdapter
    private val viewModel: ExerciseViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.exerciseViewModel = viewModel
        previousWeekAction()
        nextWeekAction()
        setWeekView()
        setUpMenu()
        setExerciseAdapter()
        navigateExerciseList()
        navigateExerciseInfo()
        return binding.root

    }

    private fun navigateExerciseInfo() {
        viewModel.navigateExerciseInfo.observe(viewLifecycleOwner) { exerciseId ->
            exerciseId?.let {
                val intent = Intent(requireContext(), ExerciseInformationActivity::class.java)
                intent.putExtra("exercise_id", it)
                startActivity(intent)
                viewModel.completeNavigateExerciseInfo()
            }
        }
    }

    private fun navigateExerciseList() {
        viewModel.navigateExerciseList.observe(requireActivity(), Observer {navigateExerciseList ->
            if(navigateExerciseList){
                val intent = Intent(requireContext(), ExerciseListActivity::class.java)
                startActivity(intent)
                viewModel.completeNavigateExerciseList()
            }
        })
    }

    private fun setExerciseAdapter(){
        exerciseAdapter = ExerciseAdapter(
            onClick = {exerciseId ->
                viewModel.onClickNavigateExerciseInfo(exerciseId)
            },
            onLongClick = {position ->
                showConfirmDialog(position)
            }
        )
        viewModel.addExerciseList.observe(viewLifecycleOwner){addList ->
            exerciseAdapter.submitList(addList)
        }
        viewModel.setAddExerciseList(getData())
        binding.rvAddExercise.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAddExercise.adapter = exerciseAdapter
    }

    private fun getData(): List<WorkoutExercises> {
        return listOf(
            WorkoutExercises(
                id = 1,
                exerciseId = 1,
                name = "Jumping Jacks",
                imageUrl = "https://example.com/images/jumping_jacks.png",
                sets = null,
                reps = null,
                restTimeSeconds = null,
                minute = 10,
                caloriesBurnt = 80f,
                startTime = null,
                endTime = null,
                planId = 101,
                type = "Cardio",
                isCompleted = false
            ),
            WorkoutExercises(
                id = 2,
                exerciseId = 2,
                name = "Push-ups",
                imageUrl = "https://example.com/images/pushups.png",
                sets = 3,
                reps = 15,
                restTimeSeconds = 60,
                minute = null,
                caloriesBurnt = 45f,
                startTime = null,
                endTime = null,
                planId = 101,
                type = "Strength",
                isCompleted = true
            ),
            WorkoutExercises(
                id = 3,
                exerciseId = 3,
                name = "Plank",
                imageUrl = "https://example.com/images/plank.png",
                sets = null,
                reps = null,
                restTimeSeconds = null,
                minute = 5,
                caloriesBurnt = 20f,
                startTime = null,
                endTime = null,
                planId = 101,
                type = "Core",
                isCompleted = true
            ),
            WorkoutExercises(
                id = 4,
                exerciseId = 4,
                name = "Barbell Squats",
                imageUrl = "https://example.com/images/barbell_squats.png",
                sets = 4,
                reps = 10,
                restTimeSeconds = 90,
                minute = null,
                caloriesBurnt = 60f,
                startTime = null,
                endTime = null,
                planId = 102,
                type = "Strength",
                isCompleted = false
            ),
            WorkoutExercises(
                id = 5,
                exerciseId = 5,
                name = "Mountain Climbers",
                imageUrl = "https://example.com/images/mountain_climbers.png",
                sets = null,
                reps = null,
                restTimeSeconds = null,
                minute = 8,
                caloriesBurnt = 72f,
                startTime = null,
                endTime = null,
                planId = 102,
                type = "Cardio",
                isCompleted = true
            ),
            WorkoutExercises(
                id = 6,
                exerciseId = 6,
                name = "Bicep Curls",
                imageUrl = "https://example.com/images/bicep_curls.png",
                sets = 3,
                reps = 12,
                restTimeSeconds = 45,
                minute = null,
                caloriesBurnt = 30f,
                startTime = null,
                endTime = null,
                planId = 103,
                type = "Strength",
                isCompleted = false
            ),
            WorkoutExercises(
                id = 7,
                exerciseId = 7,
                name = "Burpees",
                imageUrl = "https://example.com/images/burpees.png",
                sets = null,
                reps = null,
                restTimeSeconds = null,
                minute = 7,
                caloriesBurnt = 84f,
                startTime = null,
                endTime = null,
                planId = 103,
                type = "Cardio",
                isCompleted = false
            ),
            WorkoutExercises(
                id = 8,
                exerciseId = 8,
                name = "Crunches",
                imageUrl = "https://example.com/images/crunches.png",
                sets = 4,
                reps = 20,
                restTimeSeconds = 30,
                minute = null,
                caloriesBurnt = 32f,
                startTime = null,
                endTime = null,
                planId = 104,
                type = "Core",
                isCompleted = true
            ),
            WorkoutExercises(
                id = 9,
                exerciseId = 9,
                name = "Lunges",
                imageUrl = "https://example.com/images/lunges.png",
                sets = 3,
                reps = 12,
                restTimeSeconds = 60,
                minute = null,
                caloriesBurnt = 40f,
                startTime = null,
                endTime = null,
                planId = 104,
                type = "Strength",
                isCompleted = false
            ),
            WorkoutExercises(
                id = 10,
                exerciseId = 10,
                name = "Stationary Bike",
                imageUrl = "https://example.com/images/stationary_bike.png",
                sets = null,
                reps = null,
                restTimeSeconds = null,
                minute = 20,
                caloriesBurnt = 200f,
                startTime = null,
                endTime = null,
                planId = 105,
                type = "Cardio",
                isCompleted = true
            )
        )
    }

    private fun showConfirmDialog(position: Int){
        val parent = requireActivity().window.decorView as ViewGroup
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_complete_exercise_dialog, parent, false)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER
        window.attributes = windowAttributes

        dialog.setCanceledOnTouchOutside(false)

        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm_dialog)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        btnConfirm.setOnClickListener {
            val updateList = viewModel.addExerciseList.value?.toMutableList()
            updateList?.let {
                val item = it[position]
                it[position] = item.copy(isCompleted = true)
                viewModel.setAddExerciseList(it)
            }
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun setUpMenu(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarExercise)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.exercise_reminder, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.exercise_reminder ->{
                        true
                    }
                    else -> false
                }
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun setWeekView(){
        val selectedDate = CalendarUtils.selectedDate
        binding.tvMonthYear.text = CalendarUtils.monthYearFromDate(selectedDate)

        val days: ArrayList<LocalDate> = CalendarUtils.daysInWeekArray(selectedDate)

        calendarAdapter = CalendarAdapter(days, object : CalendarAdapter.OnItemListener {
            override fun onItemClick(position: Int, dayText: String, date: LocalDate?) {
                if (date != null) {
                    CalendarUtils.selectedDate = date
                    setWeekView()
                }
            }
        })

        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7, LinearLayoutManager.VERTICAL, false)
        binding.rvCalendar.adapter = calendarAdapter
    }

    private fun previousWeekAction(){
        binding.btnBackCalendar.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1)
            setWeekView()
        }
    }
    private fun formatDateVietnamese(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'tháng' M", Locale("vi", "VN"))
        return date.format(formatter)
    }

    private fun nextWeekAction(){
        binding.btnNextCalendar.setOnClickListener {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1)
            setWeekView()
        }
    }

    override fun onItemClick(position: Int, dayText: String, date: LocalDate?) {
        if(dayText != ""){
            val message = "Selected Date " + dayText + " " + CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate)
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }
}