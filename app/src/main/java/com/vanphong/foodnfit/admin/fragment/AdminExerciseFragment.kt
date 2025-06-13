package com.vanphong.foodnfit.admin.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.Model.WorkoutExercises
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.activity.AddEditExerciseActivity
import com.vanphong.foodnfit.admin.adapter.ExerciseListAdapter
import com.vanphong.foodnfit.admin.viewModel.ExerciseListViewModel
import com.vanphong.foodnfit.databinding.FragmentAdminExerciseBinding
import com.vanphong.foodnfit.factory.ListExerciseViewModelFactory
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import com.vanphong.foodnfit.repository.ExerciseRepository
import com.vanphong.foodnfit.viewModel.ListExerciseViewModel

class AdminExerciseFragment : Fragment(), OnExerciseAddListener {
    private lateinit var binding: FragmentAdminExerciseBinding
    private val repository = ExerciseRepository()
    private val viewModel: ListExerciseViewModel by viewModels{
        ListExerciseViewModelFactory(repository)
    }
    private lateinit var exerciseListAdapter: ExerciseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminExerciseBinding.inflate(layoutInflater)
        binding.fabAddExercise.setOnClickListener {
            val intent = Intent(requireContext(), AddEditExerciseActivity::class.java)
            startActivity(intent)
        }
        binding.lifecycleOwner = this
        binding.listViewModel = viewModel
        setAllExercise()
        return binding.root
    }

    private fun setAllExercise(){
        exerciseListAdapter = ExerciseListAdapter(this)
        binding.recyclerViewExercises.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.listExercises.observe(requireActivity()){list ->
            exerciseListAdapter.submitList(list)
        }

        viewModel.setListExercise()
        binding.recyclerViewExercises.adapter = exerciseListAdapter
    }

    override fun onResume() {
        super.onResume()
        setAllExercise()
    }

    override fun onAddExerciseByMinute(exercise: Exercises, minutes: Int) {
    }

    override fun onAddExerciseBySetRep(exercise: Exercises, sets: Int, reps: Int) {
    }

    override fun onRemoveExercise(workoutExercise: WorkoutExercises) {
    }
}