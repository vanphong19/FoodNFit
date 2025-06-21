package com.vanphong.foodnfit.activity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.adapter.ExerciseListAdapter
import com.vanphong.foodnfit.adapter.SelectedExercisesAdapter
import com.vanphong.foodnfit.databinding.ActivityExerciseListBinding
import com.vanphong.foodnfit.factory.ListExerciseViewModelFactory
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import com.vanphong.foodnfit.interfaces.OnExerciseListener
import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.model.SelectableExercise
import com.vanphong.foodnfit.model.WorkoutExercises
import com.vanphong.foodnfit.repository.ExerciseRepository
import com.vanphong.foodnfit.util.DialogUtils
import com.vanphong.foodnfit.viewModel.ListExerciseViewModel
import com.vanphong.foodnfit.viewModel.PlanCreationStatus
import java.math.BigDecimal
import java.math.RoundingMode

class ExerciseListActivity : BaseActivity(), OnExerciseListener {
    private var _binding: ActivityExerciseListBinding? = null
    private val binding get() = _binding!!
    private lateinit var exerciseListAdapter: ExerciseListAdapter
    private lateinit var selectedExercisesAdapter: SelectedExercisesAdapter
    private val repository = ExerciseRepository()
    private val viewModel: ListExerciseViewModel by viewModels {
        ListExerciseViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.exerciseListViewModel = viewModel
        binding.searchView.queryHint = getString(R.string.find_exercise)

        setupAdapters()
        setupObservers()
        setupClickListeners()

        viewModel.setListExercise()
    }

    private fun setupAdapters() {
        // All Exercises Adapter
        exerciseListAdapter = ExerciseListAdapter(this)
        binding.rvAllExercise.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAllExercise.adapter = exerciseListAdapter

        // Selected Exercises Adapter
        selectedExercisesAdapter = SelectedExercisesAdapter(this)
        binding.rvSelectedExercise.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectedExercise.adapter = selectedExercisesAdapter
    }

    // THAY ĐỔI QUAN TRỌNG: Logic observer để kết hợp dữ liệu
    private fun setupObservers() {
        // Lắng nghe danh sách TẤT CẢ bài tập từ ViewModel
        viewModel.listExercises.observe(this) {
            updateAllExercisesAdapter()
        }

        // Lắng nghe danh sách ĐÃ CHỌN từ ViewModel
        viewModel.selectedExercises.observe(this) { selectedList ->
            // 1. Cập nhật adapter của danh sách đã chọn
            selectedExercisesAdapter.submitList(selectedList)
            // 2. Cập nhật lại adapter của danh sách TẤT CẢ để đồng bộ dấu tick
            updateAllExercisesAdapter()
        }

        // Observer cho trạng thái tạo plan (giữ nguyên)
        viewModel.planCreationStatus.observe(this) { event ->
            event.getContentIfNotHandled()?.let { status ->
                when (status) {
                    PlanCreationStatus.LOADING -> DialogUtils.showLoadingDialog(this, "Creating plan...")
                    PlanCreationStatus.SUCCESS -> {
                        DialogUtils.hideLoadingDialog()
                        Toast.makeText(this, "Plan created successfully!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    PlanCreationStatus.ERROR -> {
                        DialogUtils.hideLoadingDialog()
                        Toast.makeText(this, "Failed to create plan.", Toast.LENGTH_SHORT).show()
                    }
                    PlanCreationStatus.IDLE -> DialogUtils.hideLoadingDialog()
                }
            }
        }
    }

    /**
     * Hàm helper để kết hợp dữ liệu và cập nhật adapter chính.
     */
    private fun updateAllExercisesAdapter() {
        val allExercises = viewModel.listExercises.value ?: return
        val selectedExercises = viewModel.selectedExercises.value ?: emptyList()

        val selectedIds = selectedExercises.map { it.exerciseId }.toSet()

        val selectableList = allExercises.map { exercise ->
            SelectableExercise(
                exercise = exercise,
                isSelected = exercise.id in selectedIds
            )
        }
        exerciseListAdapter.submitList(selectableList)
    }

    private fun setupClickListeners() {
        binding.btnMakePlan.setOnClickListener {
            if (viewModel.selectedExercises.value.isNullOrEmpty()) {
                Toast.makeText(this, "Please select at least one exercise", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.makePlan()
            }
        }
    }

    // Các hàm callback này đã đúng, giữ nguyên
    override fun onExerciseAdded(exercise: Exercises) {
        val workoutExercise = WorkoutExercises(
            // id của WorkoutExercises, có thể dùng exercise.id làm giá trị tạm thời
            // để DiffUtil phân biệt.
            id = exercise.id,
            // exerciseId là khóa ngoại trỏ đến Exercises
            exerciseId = exercise.id,
            name = exercise.name,
            imageUrl = exercise.imageUrl,
            // Lấy giá trị mặc định từ đối tượng Exercises gốc
            sets = exercise.sets,
            reps = exercise.reps,
            restTimeSeconds = exercise.restTimeSeconds,
            minute = exercise.minutes,
            caloriesBurnt = exercise.caloriesBurnt, // Lấy calo mặc định
            startTime = null,
            endTime = null,
            planId = 0,
            type = exercise.type,
            isCompleted = false
        )
        viewModel.addExercise(workoutExercise)
    }

    /**
     * Được gọi khi người dùng nhấn nút xóa.
     */
    override fun onExerciseRemoved(workoutExercise: WorkoutExercises) {
        viewModel.removeExercise(workoutExercise)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}