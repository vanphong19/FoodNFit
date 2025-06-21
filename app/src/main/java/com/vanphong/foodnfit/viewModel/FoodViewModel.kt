package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.FoodItem
import com.vanphong.foodnfit.model.FoodItemResponse
import com.vanphong.foodnfit.model.FoodLogResponse
import com.vanphong.foodnfit.model.ReminderRequest
import com.vanphong.foodnfit.model.ReminderResponse
import com.vanphong.foodnfit.model.SelectedFoodItem
import com.vanphong.foodnfit.repository.FoodLogRepository
import com.vanphong.foodnfit.repository.ReminderRepository
import com.vanphong.foodnfit.repository.UserGoalRepository
import com.vanphong.foodnfit.util.CalendarUtils
import kotlinx.coroutines.launch
import java.time.LocalDate

class FoodViewModel: ViewModel (){
    private val repository = FoodLogRepository()
    private val userGoalRepository = UserGoalRepository()
    private val reminderRepository = ReminderRepository()
    private val _allFoods = MutableLiveData<List<FoodItemResponse>>()
    val allFoods: LiveData<List<FoodItemResponse>> = _allFoods
    private val _selectedMeals = MutableLiveData<MutableList<SelectedFoodItem>>(mutableListOf())
    val selectedMeals: LiveData<List<SelectedFoodItem>> get() = _selectedMeals.map { it.toList() }
    private val _mealCount = MutableLiveData<Int>()
    val mealCount: LiveData<Int> get() = _mealCount


    // Food logs data
    private val _foodLogs = MutableLiveData<List<FoodLogResponse>>()
    val foodLogs: LiveData<List<FoodLogResponse>> = _foodLogs

    // Nutrition data
    private val _totalCalories = MutableLiveData<Double>()
    val totalCalories: LiveData<Double> = _totalCalories

    private val _totalProtein = MutableLiveData<Double>()
    val totalProtein: LiveData<Double> = _totalProtein

    private val _totalFat = MutableLiveData<Double>()
    val totalFat: LiveData<Double> = _totalFat

    private val _totalCarbs = MutableLiveData<Double>()
    val totalCarbs: LiveData<Double> = _totalCarbs

    private val _targetCalories = MutableLiveData<Double>()
    val targetCalories: LiveData<Double> = _targetCalories
    private val _targetProtein = MutableLiveData<Double>()
    val targetProtein: LiveData<Double> = _targetProtein
    private val _targetFat = MutableLiveData<Double>()
    val targetFat: LiveData<Double> = _targetFat
    private val _targetCarbs = MutableLiveData<Double>()
    val targetCarbs: LiveData<Double> = _targetCarbs



    // Meal specific data
    private val _breakfastData = MutableLiveData<FoodLogResponse?>()
    val breakfastData: LiveData<FoodLogResponse?> = _breakfastData

    private val _lunchData = MutableLiveData<FoodLogResponse?>()
    val lunchData: LiveData<FoodLogResponse?> = _lunchData

    private val _dinnerData = MutableLiveData<FoodLogResponse?>()
    val dinnerData: LiveData<FoodLogResponse?> = _dinnerData

    private val _snackData = MutableLiveData<FoodLogResponse?>()
    val snackData: LiveData<FoodLogResponse?> = _snackData

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Navigation
    private val _navigateToMealDetail = MutableLiveData<Int?>()
    val navigateToMealDetail: LiveData<Int?> get() = _navigateToMealDetail

    private val _navigateChooseFood = MutableLiveData<String?>()
    val navigationChooseFood: LiveData<String?> get() = _navigateChooseFood

    private val _createResult = MutableLiveData<Result<ReminderResponse>>()
    val createResult: LiveData<Result<ReminderResponse>> get() = _createResult

    fun createReminder(request: ReminderRequest) {
        viewModelScope.launch {
            val result = reminderRepository.create(request)
            _createResult.value = result
        }
    }

    fun onClickNavigateChooseFood(mealType: String) {
        _navigateChooseFood.value = mealType
    }

    init {
        loadUserGoal()
        loadFoodLogsForDate(CalendarUtils.selectedDate)
    }

    private fun loadUserGoal() {
        viewModelScope.launch {
            userGoalRepository.getLatest()
                .onSuccess { goal ->
                    _targetCalories.value = goal.targetCalories
                    _targetProtein.value = goal.targetProtein
                    _targetFat.value = goal.targetFat
                    _targetCarbs.value = goal.targetCarbs
                }
                .onFailure {
                    // Keep default values if API fails, or set error
                    _error.value = "Could not load user goals."
                }
        }
    }

    fun loadFoodLogsForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getFoodLogsByDate(date)
                .onSuccess { logs ->
                    _foodLogs.value = logs
                    processFoodLogs(logs)
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }

            _isLoading.value = false
        }
    }

    private fun processFoodLogs(logs: List<FoodLogResponse>) {
        // Calculate totals
        var totalCal = 0.0
        var totalProt = 0.0
        var totalFatVal = 0.0
        var totalCarbsVal = 0.0

        // Reset meal data
        _breakfastData.value = null
        _lunchData.value = null
        _dinnerData.value = null
        _snackData.value = null

        logs.forEach { log ->
            totalCal += log.totalCalories
            totalProt += log.totalProtein
            totalFatVal += log.totalFat
            totalCarbsVal += log.totalCarbs

            // Set meal specific data
            when (log.meal.lowercase()) {
                "breakfast" -> _breakfastData.value = log
                "lunch" -> _lunchData.value = log
                "dinner" -> _dinnerData.value = log
                "snack" -> _snackData.value = log
            }
        }

        _totalCalories.value = totalCal
        _totalProtein.value = totalProt
        _totalFat.value = totalFatVal
        _totalCarbs.value = totalCarbsVal
    }

    // Calculate progress percentage for calories
    fun getCaloriesProgress(): Int {
        val current = _totalCalories.value ?: 0.0
        val target = _targetCalories.value ?: 1.0
        return ((current / target) * 100).toInt().coerceIn(0, 100)
    }

    // Calculate progress percentage for macros (assuming daily targets)
    fun getProteinProgress(): Int {
        val current = _totalProtein.value ?: 0.0
        val target = 150.0 // Example target
        return ((current / target) * 100).toInt().coerceIn(0, 100)
    }

    fun getFatProgress(): Int {
        val current = _totalFat.value ?: 0.0
        val target = 85.0 // Example target
        return ((current / target) * 100).toInt().coerceIn(0, 100)
    }

    fun getCarbsProgress(): Int {
        val current = _totalCarbs.value ?: 0.0
        val target = 300.0 // Example target
        return ((current / target) * 100).toInt().coerceIn(0, 100)
    }

    fun getMealSummary(mealType: String): String {
        val meal = when (mealType.lowercase()) {
            "breakfast" -> _breakfastData.value
            "lunch" -> _lunchData.value
            "dinner" -> _dinnerData.value
            "snack" -> _snackData.value
            else -> null
        }

        return if (meal != null) {
            val foodCount = meal.foodLogDetails.size
            "${foodCount} Food - ${meal.totalCalories.toInt()} Kcal"
        } else {
            "0 Food - 0 Kcal"
        }
    }

    // Existing methods
    fun setAllFoods(data: List<FoodItemResponse>) {
        _allFoods.value = data
    }

    fun addFoodToMeal(foodItem: FoodItemResponse) {
        val currentList = _selectedMeals.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.foodItem.id == foodItem.id }

        if (index >= 0) {
            val existing = currentList[index]
            val updated = existing.copy(quantity = existing.quantity + 1)
            currentList[index] = updated
        } else {
            currentList.add(SelectedFoodItem(foodItem))
        }

        _selectedMeals.value = currentList
        setMealCount()
    }

    fun removeFoodFromMeal(foodItem: FoodItemResponse) {
        val currentList = _selectedMeals.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.foodItem.id == foodItem.id }

        if (index >= 0) {
            val existing = currentList[index]
            if (existing.quantity > 1) {
                val updated = existing.copy(quantity = existing.quantity - 1)
                currentList[index] = updated
            } else {
                currentList.removeAt(index)
            }
            _selectedMeals.value = currentList
        }
        setMealCount()
    }

    private fun setMealCount() {
        _mealCount.value = _selectedMeals.value?.size ?: 0
    }

    fun onBreakfastDetailClicked() {
        _breakfastData.value?.id?.let {
            _navigateToMealDetail.value = it
        }
    }

    fun onLunchDetailClicked() {
        _lunchData.value?.id?.let {
            _navigateToMealDetail.value = it
        }
    }

    fun onDinnerDetailClicked() {
        _dinnerData.value?.id?.let {
            _navigateToMealDetail.value = it
        }
    }

    fun onSnackDetailClicked() {
        _snackData.value?.id?.let {
            _navigateToMealDetail.value = it
        }
    }

    fun onMealDetailNavigationComplete() {
        _navigateToMealDetail.value = null
    }


    fun onChooseFoodComplete() {
        _navigateChooseFood.value = null
    }

    fun clearError() {
        _error.value = null
    }
}