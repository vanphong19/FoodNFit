package com.vanphong.foodnfit.admin.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.FoodItemRequest
import com.vanphong.foodnfit.model.FoodType
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.repository.FoodItemRepository
import com.vanphong.foodnfit.repository.FoodTypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FoodDetailViewModel(application: Application) : AndroidViewModel(application)  {
    private val foodRepository = FoodItemRepository()
    private val foodTypeRepository: FoodTypeRepository = FoodTypeRepository()
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext


    private val _foodName = MutableLiveData<String?>()
    val foodName: LiveData<String?> get() = _foodName

    private val _calories = MutableLiveData<String?>()
    val calories: LiveData<String?> get() = _calories

    private val _protein = MutableLiveData<String?>()
    val protein: LiveData<String?> get() = _protein

    private val _carbs = MutableLiveData<String?>()
    val carbs: LiveData<String?> get() = _carbs

    private val _fat = MutableLiveData<String?>()
    val fat: LiveData<String?> get() = _fat

    private val _servingSize = MutableLiveData<String?>()
    val servingSize: LiveData<String?> get() = _servingSize
    private val _foodTypes = MutableLiveData<List<FoodType>>()
    val foodTypes: LiveData<List<FoodType>> get() = _foodTypes

    private val _selectedFoodTypeId = MutableLiveData<Int?>()
    val selectedFoodTypeId: LiveData<Int?> get() = _selectedFoodTypeId

    private val _ingredients = MutableLiveData<String?>()
    val ingredients: LiveData<String?> get() = _ingredients

    private val _recipe = MutableLiveData<String?>()
    val recipe: LiveData<String?> get() = _recipe

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _createdFood = MutableLiveData<Boolean>()
    val createdFood: LiveData<Boolean> get() = _createdFood


    fun setFoodName(name: String?) {
        _foodName.value = name
    }

    fun setCalories(cal: String?) {
        _calories.value = cal
    }

    fun setProtein(value: String?) {
        _protein.value = value
    }

    fun setCarbs(value: String?) {
        _carbs.value = value
    }

    fun setFat(value: String?) {
        _fat.value = value
    }

    fun setServingSize(size: String?) {
        _servingSize.value = size
    }

    fun setIngredients(value: String?) {
        _ingredients.value = value
    }

    fun setRecipe(value: String?) {
        _recipe.value = value
    }

    fun setImageUrl(url: String?) {
        _imageUrl.value = url
    }

    fun setFoodTypes() {
        viewModelScope.launch {
            val result = foodTypeRepository.getAll()
            result.onSuccess {list ->
                _foodTypes.value = list
            }.onFailure {
                Log.d("setFoodType", it.message.toString())
            }
        }
    }
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun setSelectedFoodTypeId(id: Int?) {
        _selectedFoodTypeId.value = id
    }
    fun setFoodDetail() {
        _foodName.value = ""
        _calories.value = ""
        _protein.value = ""
        _carbs.value = ""
        _fat.value = ""
        _servingSize.value = ""
        _ingredients.value = ""
        _recipe.value = ""
        _imageUrl.value = ""
        _selectedFoodTypeId.value = foodTypes.value?.get(0)?.id
    }

    fun getById(foodId: Int){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = foodRepository.getById(foodId)
                result.onSuccess { food ->
                    _foodName.value = food.nameEn
                    _calories.value = food.calories.toString()
                    _protein.value = food.protein.toString()
                    _carbs.value = food.carbs.toString()
                    _fat.value = food.fat.toString()
                    _servingSize.value = food.servingSizeEn
                    _ingredients.value = food.ingredientsEn
                        .split(",").joinToString("\n") { "• " + it.trim() }
                    val recipeSteps = food.recipeEn
                        .split(Regex("\\d+\\."))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .mapIndexed { index, step -> "${index + 1}. $step" }
                        .joinToString("\n\n")

                    _recipe.value = recipeSteps
                    _imageUrl.value = food.imageUrl
                    _selectedFoodTypeId.value = food.foodTypeId
                }
                    .onFailure {e->
                        Log.e("FoodDetailViewModel", "Lỗi khi load food: ${e.message}")
                    }
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadImage(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = foodRepository.uploadImageFromFile(file)
                if (response.isSuccessful) {
                    val url = response.body()?.url
                    if (url != null) {
                        _imageUrl.postValue(url)  // Lưu URL ảnh public
                        _error.postValue(null)
                    } else {
                        _error.postValue("Upload ảnh thất bại: URL rỗng")
                    }
                } else {
                    _error.postValue("Upload ảnh thất bại: code ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Upload ảnh thất bại: ${e.message}")
            }
        }
    }

    fun createFood(){
        val request = getRequest() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = foodRepository.create(request)
                response.onSuccess {
                    _createdFood.postValue(true)
                    _error.postValue(null)
                }.onFailure {
                    _createdFood.postValue(false)
                    _error.postValue(context.getString(R.string.create_food_failed) + ": code ${it.message}")
                }
            } catch (e: Exception) {
                _createdFood.postValue(false)
                _error.postValue(context.getString(R.string.create_food_failed) + ": ${e.message}")
            }
        }
    }

    fun updateFood(foodId: Int){
        val request = getRequest() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = foodRepository.update(foodId, request)
                response.onSuccess {
                    _createdFood.postValue(true)
                    _error.postValue(null)
                }.onFailure {
                    _createdFood.postValue(false)
                    _error.postValue(context.getString(R.string.update_food_failed) + ": code ${it.message}")
                }
            } catch (e: Exception) {
                _createdFood.postValue(false)
                _error.postValue(context.getString(R.string.update_food_failed) + ": ${e.message}")
            }
        }
    }

    private fun getRequest(): FoodItemRequest? {
        val foodNameStr = _foodName.value ?: ""
        val calories = _calories.value?.toDoubleOrNull()
        val protein = _protein.value?.toDoubleOrNull()
        val carbs = _carbs.value?.toDoubleOrNull()
        val fat = _fat.value?.toDoubleOrNull()
        val servingSizeStr = _servingSize.value ?: ""
        val selectedFoodTypeIdInt = _selectedFoodTypeId.value
        val ingredientsStr = _ingredients.value ?: ""
        val recipeStr = _recipe.value ?: ""
        val imgUrl = _imageUrl.value ?: ""

        if (foodNameStr.isBlank()) {
            _error.value = context.getString(R.string.empty_name)
            return null
        }
        if (calories == null) {
            _error.value = context.getString(R.string.empty_calories)
            return null
        }
        if (protein == null) {
            _error.value = context.getString(R.string.empty_protein)
            return null
        }
        if (carbs == null) {
            _error.value = context.getString(R.string.empty_carbs)
            return null
        }
        if (fat == null) {
            _error.value = context.getString(R.string.empty_fat)
            return null
        }
        if (servingSizeStr.isBlank()) {
            _error.value = context.getString(R.string.empty_serving_size)
            return null
        }
        if (selectedFoodTypeIdInt == null) {
            _error.value = context.getString(R.string.empty_food_type)
            return null
        }
        if (ingredientsStr.isBlank()) {
            _error.value = context.getString(R.string.empty_ingredients)
            return null
        }
        if (recipeStr.isBlank()) {
            _error.value = context.getString(R.string.empty_recipe)
            return null
        }
        if (imgUrl.isBlank()) {
            _error.value = context.getString(R.string.empty_image)
            return null
        }

        return FoodItemRequest(
            nameEn = foodNameStr,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            imageUrl = imgUrl,
            servingSizeEn = servingSizeStr,
            recipeEn = recipeStr,
            foodTypeId = selectedFoodTypeIdInt,
            ingredientsEn = ingredientsStr
        )
    }
}
