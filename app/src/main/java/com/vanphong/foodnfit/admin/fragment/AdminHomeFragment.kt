package com.vanphong.foodnfit.admin.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.activity.AddEditExerciseActivity
import com.vanphong.foodnfit.admin.activity.AddEditFoodActivity
import com.vanphong.foodnfit.databinding.FragmentAdminHomeBinding
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.ExerciseRepository
import com.vanphong.foodnfit.repository.FeedbackRepository
import com.vanphong.foodnfit.repository.FoodItemRepository
import com.vanphong.foodnfit.repository.UserRepository
import com.vanphong.foodnfit.util.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdminHomeFragment : Fragment() {
    private lateinit var binding: FragmentAdminHomeBinding
    private val userRepository by lazy { UserRepository() }
    private val foodRepository by lazy { FoodItemRepository() }
    private val exerciseRepository by lazy { ExerciseRepository() }
    private val feedbackRepository by lazy { FeedbackRepository() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminHomeBinding.inflate(layoutInflater)
        binding.tvCurrentDate.text = getCurrentVietnameseDate()
        fetchUserCount()
        fetchFoodCount()
        fetchExerciseCount()
        fetchFeedbackCount()
        binding.btnAddFood.setOnClickListener {
            val intent = Intent(requireContext(), AddEditFoodActivity::class.java)
            intent.putExtra("food_id", 0)
            intent.putExtra("title", getString(R.string.add_food))
            startActivity(intent)
        }
        binding.btnAddExercise.setOnClickListener {
            val intent = Intent(requireContext(), AddEditExerciseActivity::class.java)
            intent.putExtra("exercise_id", 0)
            intent.putExtra("title", getString(R.string.add_exercise))
            startActivity(intent)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun fetchUserCount(){
        viewLifecycleOwner.lifecycleScope.launch {
            val countTotal = withContext(Dispatchers.IO){
                userRepository.countUsers()
            }
            Log.d("count", countTotal.toString())
            countTotal.onSuccess {
                binding.tvTotalUsers.text = it.toString()
            }
            .onFailure {
                Toast.makeText(requireContext(), "Lỗi tổng: ${it.message}", Toast.LENGTH_SHORT).show()
            }

            val countThisMonthResult = withContext(Dispatchers.IO) {
                userRepository.countUsersThisMonth()
            }
            countThisMonthResult.onSuccess {
                binding.tvUserThisMonth.text = "+$it"
            }.onFailure {
                Toast.makeText(requireContext(), "Lỗi tháng này: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchFoodCount(){
        viewLifecycleOwner.lifecycleScope.launch {
            val countTotal = withContext(Dispatchers.IO){
                foodRepository.getFoodCount()
            }
            Log.d("count", countTotal.toString())
            countTotal.onSuccess {
                binding.tvTotalFoods.text = it.toString()
            }
                .onFailure {
                    Toast.makeText(requireContext(), "Lỗi tổng: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            val countThisMonthResult = withContext(Dispatchers.IO) {
                foodRepository.countFoodThisMonth()
            }
            countThisMonthResult.onSuccess {
                binding.tvFoodThisMonth.text = "+$it"
            }.onFailure {
                Toast.makeText(requireContext(), "Lỗi tháng này: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun fetchExerciseCount(){
        viewLifecycleOwner.lifecycleScope.launch {
            val countTotal = withContext(Dispatchers.IO){
                exerciseRepository.count()
            }
            Log.d("count", countTotal.toString())
            countTotal.onSuccess {
                binding.tvTotalExercises.text = it.toString()
            }
                .onFailure {
                    Toast.makeText(requireContext(), "Lỗi tổng: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            val countThisMonthResult = withContext(Dispatchers.IO) {
                exerciseRepository.countExerciseThisMonth()
            }
            countThisMonthResult.onSuccess {
                binding.tvExerciseThisMonth.text = "+$it"
            }.onFailure {
                Toast.makeText(requireContext(), "Lỗi tháng này: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchFeedbackCount(){
        viewLifecycleOwner.lifecycleScope.launch {
            val countTotal = withContext(Dispatchers.IO){
                feedbackRepository.count()
            }
            Log.d("count", countTotal.toString())
            countTotal.onSuccess {
                binding.tvFeedback.text = it.toString()
            }
                .onFailure {
                    Toast.makeText(requireContext(), "Lỗi tổng: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getCurrentVietnameseDate(): String{
        val currentDate = LocalDate.now()
        val vietnameseLocale = Locale("vi","VN")
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'tháng' M", vietnameseLocale)
        return currentDate.format(formatter)
    }

    override fun onResume() {
        super.onResume()
        fetchUserCount()
        fetchFoodCount()
        fetchFeedbackCount()
        fetchExerciseCount()
    }
}