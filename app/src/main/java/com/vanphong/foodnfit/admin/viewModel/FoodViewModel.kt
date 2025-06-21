package com.vanphong.foodnfit.admin.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.FoodItemResponse
import com.vanphong.foodnfit.repository.FoodItemRepository
import kotlinx.coroutines.launch

class FoodViewModel: ViewModel() {
    private val foodRepository = FoodItemRepository()
    private val _foodList = MutableLiveData<List<FoodItemResponse>>()
    val foodList: LiveData<List<FoodItemResponse>> get() = _foodList
    private val _search = MutableLiveData<String?>()
    val search: LiveData<String?> get() = _search

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _notify = MutableLiveData<String>()
    val notify: LiveData<String> get() = _notify

    fun setSearch(keyword: CharSequence?) {
        _search.value = keyword?.toString()?.trim()
        refreshList()
    }

    private var currentPage = 0
    private var isLastPage = false
    private val pageSize = 10

    fun loadMoreFoodItems() {
        if (_isLoading.value == true || isLastPage) return

        viewModelScope.launch {
            _isLoading.value = true

            val result = foodRepository.getAllFoods(
                search = _search.value ?: "", currentPage, pageSize)
            result.onSuccess {
                val currentList = _foodList.value ?: emptyList()
                _foodList.value = currentList + it.content

                // cập nhật trạng thái phân trang
                isLastPage = it.number + 1 >= it.totalPages
                currentPage++
            }.onFailure {
                Log.e("FoodItem", "Error loading more", it)
            }
            _isLoading.value = false
        }
    }

    fun removeFood(foodId: Int) {
        viewModelScope.launch {
            val result = try {
                foodRepository.remove(foodId)
            } catch (e: Exception) {
                Result.failure<String>(e)
            }
            result.onSuccess { message ->
                _notify.postValue(message) // Giả sử bạn dùng LiveData để báo UI
                refreshList()
            }.onFailure { error ->
                _notify.postValue(error.message)
            }
        }
    }

    fun refreshList() {
        currentPage = 0
        isLastPage = false
        _foodList.value = emptyList()
        loadMoreFoodItems()
    }
}