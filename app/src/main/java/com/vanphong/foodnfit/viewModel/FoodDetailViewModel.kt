package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.model.Ingredient

import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.FoodItemResponse // Import đúng model
import com.vanphong.foodnfit.repository.FoodItemRepository
import kotlinx.coroutines.launch

class FoodDetailViewModel: ViewModel() {
    private val repository = FoodItemRepository() // Tạo instance của repository

    // Thay đổi LiveData để làm việc với FoodItemResponse
    private val _foodItem = MutableLiveData<FoodItemResponse>()
    val foodItem: LiveData<FoodItemResponse> = _foodItem

    private val _ingredients = MutableLiveData<List<Ingredient>>()
    val ingredients: LiveData<List<Ingredient>> = _ingredients

    private val _recipeSteps = MutableLiveData<List<String>>()
    val recipeSteps: LiveData<List<String>> = _recipeSteps

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Hàm để tải chi tiết món ăn từ API bằng ID
     */
    fun loadFoodDetail(foodId: Int) {
        viewModelScope.launch {
            repository.getById(foodId)
                .onSuccess { foodDetail ->
                    _foodItem.value = foodDetail
                    // Xử lý chuỗi ingredients và recipe sau khi có dữ liệu
                    processIngredients(foodDetail.ingredientsEn)
                    processRecipe(foodDetail.recipeEn)
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load food detail."
                }
        }
    }

    /**
     * Xử lý chuỗi nguyên liệu thành một danh sách
     */
    private fun processIngredients(ingredientsString: String?) {
        if (ingredientsString.isNullOrEmpty()) {
            _ingredients.value = emptyList()
            return
        }
        // Giả sử các nguyên liệu được phân tách bằng dấu phẩy ","
        val ingredientList = ingredientsString.split(",").map {
            val parts = it.trim().split(" ").toMutableList()
            if (parts.size > 1) {
                val size = parts.removeLast() // Lấy phần tử cuối làm size
                val name = parts.joinToString(" ") // Phần còn lại là tên
                Ingredient(name, size)
            } else {
                Ingredient(it.trim(), "") // Nếu không có size
            }
        }
        _ingredients.value = ingredientList
    }

    /**
     * Xử lý chuỗi công thức thành các bước
     */
    private fun processRecipe(recipeString: String?) {
        if (recipeString.isNullOrEmpty()) {
            _recipeSteps.value = emptyList()
            return
        }
        // Giả sử các bước được phân tách bằng xuống dòng và số "1.", "2."
        val steps = recipeString.split(Regex("\\n\\d+\\.\\s*")).map { it.trim() }
        _recipeSteps.value = steps
    }
}