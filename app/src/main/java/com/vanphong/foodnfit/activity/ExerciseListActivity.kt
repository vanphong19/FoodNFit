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
import com.vanphong.foodnfit.interfaces.OnExerciseAddListener
import com.vanphong.foodnfit.viewModel.ListExerciseViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class ExerciseListActivity : BaseActivity(), OnExerciseAddListener {
    private var _binding: ActivityExerciseListBinding? = null
    private val binding get() = _binding!!
    private lateinit var exerciseListAdapter: ExerciseListAdapter
    private lateinit var selectedExercisesAdapter: SelectedExercisesAdapter
    private val viewModel: ListExerciseViewModel by viewModels()
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

        viewModel.setListExercise(getData())
        binding.rvAllExercise.adapter = exerciseListAdapter
    }

    private fun setSelectedExercise(){
        selectedExercisesAdapter = SelectedExercisesAdapter(this)
        binding.rvSelectedExercise.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectedExercise.adapter = selectedExercisesAdapter
    }
    private fun getData(): List<Exercises> {
        return listOf(
            Exercises(
                id = 1,
                name = "Jumping Jacks",
                description = "Bài tập khởi động giúp làm nóng toàn thân và tăng nhịp tim.",
                videoUrl = "https://example.com/videos/jumping_jacks.mp4",
                imageUrl = "https://example.com/images/jumping_jacks.png",
                difficultyLevel = "Beginner",
                muscleGroup = "Full Body",
                caloriesBurnt = 8.0f,
                minutes = 10,
                sets = null,
                reps = null,
                restTimeSeconds = null,
                equipmentRequired = "None",
                note = "Giữ lưng thẳng và thở đều.",
                type = "Cardio",
                isActive = true
            ),
            Exercises(
                id = 2,
                name = "Push-ups",
                description = "Bài tập cơ ngực, tay sau và core.",
                videoUrl = "https://example.com/videos/pushups.mp4",
                imageUrl = "https://example.com/images/pushups.png",
                difficultyLevel = "Intermediate",
                muscleGroup = "Chest",
                caloriesBurnt = 5.0f,
                minutes = null,
                sets = 3,
                reps = 15,
                restTimeSeconds = 60,
                equipmentRequired = "None",
                note = "Đừng để bụng chạm sàn.",
                type = "Strength",
                isActive = true
            ),
            Exercises(
                id = 3,
                name = "Plank",
                description = "Tăng cường cơ bụng và lưng dưới.",
                videoUrl = "https://example.com/videos/plank.mp4",
                imageUrl = "https://example.com/images/plank.png",
                difficultyLevel = "Beginner",
                muscleGroup = "Core",
                caloriesBurnt = 4.0f,
                minutes = 5,
                sets = null,
                reps = null,
                restTimeSeconds = null,
                equipmentRequired = "None",
                note = "Giữ vai, hông và gót chân thành một đường thẳng.",
                type = "Core",
                isActive = true
            ),
            Exercises(
                id = 4,
                name = "Barbell Squats",
                description = "Bài tập sức mạnh cho đùi và mông với tạ đòn.",
                videoUrl = "https://example.com/videos/barbell_squats.mp4",
                imageUrl = "https://example.com/images/barbell_squats.png",
                difficultyLevel = "Advanced",
                muscleGroup = "Legs",
                caloriesBurnt = 6.5f,
                minutes = null,
                sets = 4,
                reps = 10,
                restTimeSeconds = 90,
                equipmentRequired = "Barbell",
                note = "Hạ mông xuống song song sàn.",
                type = "Strength",
                isActive = true
            ),
            Exercises(
                id = 5,
                name = "Mountain Climbers",
                description = "Tăng nhịp tim và đốt cháy calo nhanh.",
                videoUrl = "https://example.com/videos/mountain_climbers.mp4",
                imageUrl = "https://example.com/images/mountain_climbers.png",
                difficultyLevel = "Intermediate",
                muscleGroup = "Full Body",
                caloriesBurnt = 9.0f,
                minutes = 8,
                sets = null,
                reps = null,
                restTimeSeconds = null,
                equipmentRequired = "None",
                note = "Giữ tay cố định, đẩy gối nhanh như chạy.",
                type = "Cardio",
                isActive = true
            ),
            Exercises(
                id = 6,
                name = "Bicep Curls",
                description = "Tăng cơ tay trước với tạ đơn.",
                videoUrl = "https://example.com/videos/bicep_curls.mp4",
                imageUrl = "https://example.com/images/bicep_curls.png",
                difficultyLevel = "Beginner",
                muscleGroup = "Arms",
                caloriesBurnt = 3.5f,
                minutes = null,
                sets = 3,
                reps = 12,
                restTimeSeconds = 45,
                equipmentRequired = "Dumbbells",
                note = "Không đánh người khi nâng.",
                type = "Strength",
                isActive = true
            ),
            Exercises(
                id = 7,
                name = "Burpees",
                description = "Bài tập toàn thân kết hợp squat, push-up và nhảy.",
                videoUrl = "https://example.com/videos/burpees.mp4",
                imageUrl = "https://example.com/images/burpees.png",
                difficultyLevel = "Advanced",
                muscleGroup = "Full Body",
                caloriesBurnt = 12.0f,
                minutes = 7,
                sets = null,
                reps = null,
                restTimeSeconds = null,
                equipmentRequired = "None",
                note = "Cố gắng thực hiện liên tục không nghỉ.",
                type = "Cardio",
                isActive = true
            ),
            Exercises(
                id = 8,
                name = "Crunches",
                description = "Tập trung vào cơ bụng trên.",
                videoUrl = "https://example.com/videos/crunches.mp4",
                imageUrl = "https://example.com/images/crunches.png",
                difficultyLevel = "Beginner",
                muscleGroup = "Abs",
                caloriesBurnt = 2.8f,
                minutes = null,
                sets = 4,
                reps = 20,
                restTimeSeconds = 30,
                equipmentRequired = "None",
                note = "Không dùng lực cổ để gập.",
                type = "Core",
                isActive = true
            ),
            Exercises(
                id = 9,
                name = "Lunges",
                description = "Bài tập cơ đùi và mông hiệu quả.",
                videoUrl = "https://example.com/videos/lunges.mp4",
                imageUrl = "https://example.com/images/lunges.png",
                difficultyLevel = "Intermediate",
                muscleGroup = "Legs",
                caloriesBurnt = 6.0f,
                minutes = null,
                sets = 3,
                reps = 12,
                restTimeSeconds = 60,
                equipmentRequired = "None",
                note = "Giữ thẳng lưng, đầu gối không vượt quá mũi chân.",
                type = "Strength",
                isActive = true
            ),
            Exercises(
                id = 10,
                name = "Stationary Bike",
                description = "Đạp xe tại chỗ giúp đốt mỡ và tăng sức bền.",
                videoUrl = "https://example.com/videos/stationary_bike.mp4",
                imageUrl = "https://example.com/images/stationary_bike.png",
                difficultyLevel = "Beginner",
                muscleGroup = "Legs",
                caloriesBurnt = 10.0f,
                minutes = 20,
                sets = null,
                reps = null,
                restTimeSeconds = null,
                equipmentRequired = "Stationary Bike",
                note = "Giữ tốc độ ổn định và kiểm soát nhịp thở.",
                type = "Cardio",
                isActive = true
            )
        )
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