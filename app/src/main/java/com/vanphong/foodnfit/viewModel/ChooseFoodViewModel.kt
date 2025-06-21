package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.*
import com.vanphong.foodnfit.repository.FoodItemRepository
import com.vanphong.foodnfit.repository.FoodLogDetailRepository
import com.vanphong.foodnfit.repository.FoodLogRepository
import com.vanphong.foodnfit.repository.UserGoalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class ChooseFoodViewModel: ViewModel() {
    // Khởi tạo các repository
    private val foodRepository = FoodItemRepository()
    private val foodLogRepository = FoodLogRepository()
    private val foodLogDetailRepository = FoodLogDetailRepository()
    private val userGoalRepository = UserGoalRepository()

    private val _allFoods = MutableLiveData<List<FoodItemResponse>>()
    val allFoods: LiveData<List<FoodItemResponse>> = _allFoods

    private val _selectedMeals = MutableLiveData<MutableList<SelectedFoodItem>>(mutableListOf())
    val selectedMeals: LiveData<List<SelectedFoodItem>> get() = _selectedMeals.map { it.toList() }

    val mealCount: LiveData<Int> = _selectedMeals.map { it.size }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _saveSuccess = MutableLiveData<Boolean>(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private var searchJob: Job? = null
    private var currentPage = 0
    private var isLastPage = false
    private var isLoadingMore = false
    private var currentQuery: String? = null

    private fun fetchFoods(query: String?, isNewSearch: Boolean) {
        if (isNewSearch) {
            currentPage = 0
            isLastPage = false
            currentQuery = query
        }

        if (isLoadingMore || isLastPage) {
            return
        }

        isLoadingMore = true

        viewModelScope.launch {
            foodRepository.getFoods(query, page = currentPage)
                .onSuccess { newPage ->
                    isLastPage = newPage.number >= newPage.totalPages - 1 || newPage.content.isEmpty()

                    val currentList = if (isNewSearch) {
                        emptyList()
                    } else {
                        _allFoods.value ?: emptyList()
                    }

                    _allFoods.value = currentList + newPage.content
                    currentPage++
                }
                .onFailure { exception ->
                    if (exception !is kotlinx.coroutines.CancellationException) {
                        _error.value = exception.message ?: "Failed to load food list."
                    }
                }

            isLoadingMore = false
        }
    }

    fun loadInitialData() {
        fetchFoods(null, isNewSearch = true)
    }

    fun search(query: String?) {
        searchJob?.cancel() // Hủy job cũ
        searchJob = viewModelScope.launch {
            delay(500L) // Chờ 500ms
            fetchFoods(query, isNewSearch = true)
        }
    }

    fun loadNextPage() {
        fetchFoods(currentQuery, isNewSearch = false)
    }

    fun addFoodToMeal(foodItem: FoodItemResponse) {
        val currentList = _selectedMeals.value ?: emptyList()
        val index = currentList.indexOfFirst { it.foodItem.id == foodItem.id }

        val newList = currentList.toMutableList()

        if (index != -1) {
            val oldItem = newList[index]
            val newItem = oldItem.copy(quantity = oldItem.quantity + 1)
            newList[index] = newItem
        } else {
            newList.add(SelectedFoodItem(foodItem, 1))
        }

        _selectedMeals.value = newList
    }

    fun removeFoodFromMeal(selectedFoodItem: SelectedFoodItem) {
        val currentList = _selectedMeals.value ?: return
        val index = currentList.indexOfFirst { it.foodItem.id == selectedFoodItem.foodItem.id }

        if (index == -1) return

        val newList = currentList.toMutableList()
        val itemToRemove = newList[index]

        if (itemToRemove.quantity > 1) {
            val updatedItem = itemToRemove.copy(quantity = itemToRemove.quantity - 1)
            newList[index] = updatedItem
        } else {
            newList.removeAt(index)
        }

        _selectedMeals.value = newList
    }

    fun saveSelectedMeal(mealType: String) {
        val selectedItems = _selectedMeals.value
        if (selectedItems.isNullOrEmpty()) {
            _error.value = "Vui lòng chọn ít nhất một món ăn."
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            userGoalRepository.getLatest()
                .onSuccess { userGoal ->
                    Log.d("getlatest", userGoal.toString())
                    val totalCalories = selectedItems.sumOf { it.totalCalories }
                    val targetCaloriesForMeal = when (mealType.uppercase()) {
                        "BREAKFAST" -> userGoal.caloriesBreakfast
                        "LUNCH" -> userGoal.caloriesLunch
                        "DINNER" -> userGoal.caloriesDinner
                        "SNACK" -> userGoal.caloriesSnack
                        else -> 0.0
                    }
                    val allowedDifference = targetCaloriesForMeal * 0.20

                    if (totalCalories > (targetCaloriesForMeal + allowedDifference)) {
                        _error.value = "Bữa ăn này có lượng calo quá nhiều! Khuyến nghị: khoảng ${targetCaloriesForMeal.toInt()} kcal. Vui lòng giảm bớt."
                        _isLoading.value = false
                        return@onSuccess
                    }

                    proceedToSave(mealType, selectedItems)
                }
                .onFailure { exception ->
                    _error.value = "Không thể kiểm tra mục tiêu calo. Vui lòng thử lại."
                    _isLoading.value = false
                }
        }
    }


    private fun proceedToSave(mealType: String, selectedItems: List<SelectedFoodItem>) {
        viewModelScope.launch {
            // Logic lưu đã có sẵn, chỉ cần copy vào đây
            val totalCalories = selectedItems.sumOf { it.totalCalories }
            val totalProtein = selectedItems.sumOf { it.totalProtein }
            val totalFat = selectedItems.sumOf { it.totalFat }
            val totalCarbs = selectedItems.sumOf { it.totalCarbs }

            val foodLogRequest = FoodLogRequest(totalCalories, totalProtein, totalFat, totalCarbs, mealType.uppercase())

            foodLogRepository.create(foodLogRequest)
                .onSuccess { foodLogResponse ->
                    val detailRequests = selectedItems.map {
                        FoodLogDetailRequest(it.foodItem.id, "${it.quantity} serving(s)", it.totalCalories, it.totalCarbs, it.totalProtein, it.totalFat)
                    }
                    val batchRequest = FoodLogDetailBatchRequest(foodLogResponse.id, detailRequests)

                    foodLogDetailRepository.saveBatch(batchRequest)
                        .onSuccess {
                            _saveSuccess.value = true
                            _selectedMeals.value = mutableListOf()
                        }
                        .onFailure {
                            _error.value = "Lưu chi tiết bữa ăn thất bại."
                        }
                }
                .onFailure {
                    _error.value = "Tạo nhật ký bữa ăn thất bại."
                }

            _isLoading.value = false
        }
    }

    fun onSaveComplete() {
        _saveSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
}