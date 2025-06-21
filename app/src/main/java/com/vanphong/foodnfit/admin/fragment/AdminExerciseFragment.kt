package com.vanphong.foodnfit.admin.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.model.WorkoutExercises
import com.vanphong.foodnfit.admin.activity.AddEditExerciseActivity
import com.vanphong.foodnfit.admin.activity.ExerciseInfoActivity
import com.vanphong.foodnfit.admin.adapter.ExerciseListAdapter
import com.vanphong.foodnfit.databinding.FragmentAdminExerciseBinding
import com.vanphong.foodnfit.factory.ListExerciseViewModelFactory
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import com.vanphong.foodnfit.repository.ExerciseRepository
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.ListExerciseViewModel

class AdminExerciseFragment : Fragment(), OnExerciseAddListener {
    private lateinit var binding: FragmentAdminExerciseBinding
    private val repository = ExerciseRepository()
    private val viewModel: ListExerciseViewModel by viewModels{
        ListExerciseViewModelFactory(repository)
    }
    private lateinit var exerciseListAdapter: ExerciseListAdapter

    private val exerciseInfoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Load lại danh sách bài tập
            viewModel.setListExercise(refresh = true)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminExerciseBinding.inflate(layoutInflater)
        binding.fabAddExercise.setOnClickListener {
            val intent = Intent(requireContext(), AddEditExerciseActivity::class.java)
            intent.putExtra("exercise_id", 0)
            intent.putExtra("title", getString(R.string.add_exercise))
            exerciseInfoLauncher.launch(intent)
        }
        binding.lifecycleOwner = this
        binding.listViewModel = viewModel
        setAllExercise()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                DialogUtils.showLoadingDialog(requireActivity(), getString(R.string.loading))
            } else {
                DialogUtils.hideLoadingDialog()
            }
        }
        return binding.root
    }

    private fun setAllExercise(){
        exerciseListAdapter = ExerciseListAdapter { exerciseId ->
            val intent = Intent(requireContext(), ExerciseInfoActivity::class.java)
            intent.putExtra("exercise_id", exerciseId)
            intent.putExtra("title", getString(R.string.update_exercise))
            exerciseInfoLauncher.launch(intent)
        }
        binding.recyclerViewExercises.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.listExercises.observe(viewLifecycleOwner) { list ->
            exerciseListAdapter.submitList(list)
        }

        viewModel.setListExercise(refresh = true)
        binding.recyclerViewExercises.adapter = exerciseListAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListExercise(refresh = true)
    }

    override fun onAddExerciseByMinute(exercise: Exercises, minutes: Int) {
    }

    override fun onAddExerciseBySetRep(exercise: Exercises, sets: Int, reps: Int) {
    }

    override fun onRemoveExercise(workoutExercise: WorkoutExercises) {
    }
}