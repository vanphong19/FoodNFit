package com.vanphong.foodnfit.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.Model.WorkoutExercises
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.ExerciseListAdapter
import com.vanphong.foodnfit.adapter.SelectedExercisesAdapter
import com.vanphong.foodnfit.databinding.ActivityExerciseListBinding
import com.vanphong.foodnfit.factory.ListExerciseViewModelFactory
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import com.vanphong.foodnfit.repository.ExerciseRepository
import com.vanphong.foodnfit.viewModel.ListExerciseViewModel
import java.math.BigDecimal
import java.math.RoundingMode

    class ExerciseListActivity : BaseActivity(), OnExerciseAddListener {
    private var _binding: ActivityExerciseListBinding? = null
    private val binding get() = _binding!!
    private lateinit var exerciseListAdapter: ExerciseListAdapter
    private lateinit var selectedExercisesAdapter: SelectedExercisesAdapter
    private val repository = ExerciseRepository()
    private val viewModel: ListExerciseViewModel by viewModels{
        ListExerciseViewModelFactory(repository)
    }
    private val selectedExercises = mutableListOf<WorkoutExercises>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.exerciseListViewModel = viewModel
        binding.searchView.queryHint = getString(R.string.find_exercise)
        setAllExercise()
        setSelectedExercise()
    }

    private fun setAllExercise(){
        exerciseListAdapter = ExerciseListAdapter(this)
        binding.rvAllExercise.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.listExercises.observe(this){list ->
            exerciseListAdapter.submitList(list)
        }

        viewModel.setListExercise()
        binding.rvAllExercise.adapter = exerciseListAdapter
    }

    private fun setSelectedExercise(){
        selectedExercisesAdapter = SelectedExercisesAdapter(this)
        binding.rvSelectedExercise.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectedExercise.adapter = selectedExercisesAdapter
    }

    override fun onAddExerciseByMinute(exercise: Exercises, minutes: Int) {
        val rawCaloriesBurnt = (exercise.caloriesBurnt / (exercise.minutes ?: 1)) * minutes
        val caloriesBurnt = BigDecimal(rawCaloriesBurnt.toDouble())
            .setScale(2, RoundingMode.HALF_UP)
            .toFloat()

        val workoutExercise = WorkoutExercises(
            id = 1,  // ID tạm thời
            exerciseId = exercise.id,
            name = exercise.name,
            imageUrl = exercise.imageUrl,
            sets = null,
            reps = null,
            restTimeSeconds = exercise.restTimeSeconds,
            minute = minutes,
            caloriesBurnt = caloriesBurnt,
            startTime = null,
            endTime = null,
            planId = 0,
            type = exercise.type,
            isCompleted = false
        )

        val index = selectedExercises.indexOfFirst { it.exerciseId == workoutExercise.exerciseId }
        if (index != -1) {
            selectedExercises[index] = workoutExercise
        } else {
            selectedExercises.add(workoutExercise)
        }

        selectedExercisesAdapter.submitList(selectedExercises.toList())
    }


    override fun onAddExerciseBySetRep(exercise: Exercises, sets: Int, reps: Int) {
//        val caloriesBurnt = (exercise.caloriesBurnt / (exercise.minutes ?: 1)) * minutes
        val workoutExercise = WorkoutExercises(
            id = 1,  // Hàm tạo ID tạm thời
            exerciseId = exercise.id,
            name = exercise.name,
            imageUrl = exercise.imageUrl,
            sets = sets,
            reps = reps,
            restTimeSeconds = exercise.restTimeSeconds,
            minute = null,
            caloriesBurnt = exercise.caloriesBurnt,
            startTime = null,
            endTime = null,
            planId = 0, // gán sau
            type = exercise.type,
            isCompleted = false
        )
        val index = selectedExercises.indexOfFirst { it.id == workoutExercise.id }
        if(index != -1){
            selectedExercises[index] = workoutExercise
        }
        else{
            selectedExercises.add(workoutExercise)
        }
        selectedExercisesAdapter.submitList(selectedExercises.toList())
    }

    override fun onRemoveExercise(workoutExercise: WorkoutExercises) {
        selectedExercises.remove(workoutExercise)
        selectedExercisesAdapter.submitList(selectedExercises.toList())
    }
}