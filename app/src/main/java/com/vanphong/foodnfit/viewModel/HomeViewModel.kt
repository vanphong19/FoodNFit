package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanphong.foodnfit.model.HourlyStepSummary
import com.vanphong.foodnfit.model.StepSummary
import com.vanphong.foodnfit.repository.StepsTrackingRepository
import com.vanphong.foodnfit.repository.UserProfileRepository
import com.vanphong.foodnfit.repository.UserRepository
import com.vanphong.foodnfit.repository.WaterIntakeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel:ViewModel() {
    val repository = StepsTrackingRepository()
    private val userRepository = UserRepository()
    private val profileRepository =UserProfileRepository()
    private val waterIntakeRepository = WaterIntakeRepository()
    private val _todaySteps = MutableLiveData<StepSummary>()
    val todaySteps: LiveData<StepSummary> = _todaySteps

    private val _height = MutableLiveData<String>()
    val height: LiveData<String> get() = _height
    private val _weight = MutableLiveData<String>()
    val weight: LiveData<String> get() = _weight
    private val _cups = MutableLiveData<Int>()
    val cups: LiveData<Int> get() = _cups
    private val _tdee = MutableLiveData<String>()
    val tdee: LiveData<String> get() = _tdee
    private val _caloriesIn = MutableLiveData<String>()
    val caloriesIn: LiveData<String> get() = _caloriesIn
    private val _caloriesOut = MutableLiveData<String>()
    val caloriesOut: LiveData<String> get() = _caloriesOut
    private val _protein = MutableLiveData<String>()
    val protein: LiveData<String> get() = _protein
    private val _fat = MutableLiveData<String>()
    val fat: LiveData<String> get() = _fat
    private val _carbs = MutableLiveData<String>()
    val carbs: LiveData<String> get() = _carbs
    private val _bmi = MutableLiveData<String>()
    val bmi: LiveData<String> get() = _bmi
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    private val _hourlySteps = MutableLiveData<List<HourlyStepSummary>>()
    val hourlySteps: LiveData<List<HourlyStepSummary>> = _hourlySteps

    fun loadHourlySteps() {
        viewModelScope.launch {
            try {
                // Gọi API để lấy dữ liệu theo giờ
                val result = repository.getHourlySummary() // Bạn cần implement function này trong repository
                result.onSuccess { hourlyDataList ->
                    _hourlySteps.value = hourlyDataList
                    _error.value = null
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun parseHourlyStepsFromJson(jsonString: String): List<HourlyStepSummary> {
        val gson = Gson()
        val listType = object : TypeToken<List<HourlyStepSummary>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    fun loadTodaySteps(){

        viewModelScope.launch {
            try {
                val result = repository.getTodaySummary()
                result.onSuccess {
                    _todaySteps.value = it
                    _error.value = null
                }.onFailure {exception ->
                    _error.value = exception.message
                }

            }
            finally {
            }
        }
    }

    fun startAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                loadTodaySteps()
                delay(30000) // 30 giây
            }
        }
    }

    fun loadCalories(){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.getUserStatsToday()
                result.onSuccess {
                    _caloriesIn.value = it.caloriesIn.toString()
                    _caloriesOut.value = it.caloriesOut.toString()
                    _tdee.value = it.tdee.toString()
                    _protein.value = it.protein.toString()
                    _fat.value = it.fat.toString()
                    _carbs.value = it.carbs.toString()
                }.onFailure {
                    _error.value = "lỗi ${it.message}"
                }
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun loadInfo(){
        viewModelScope.launch {
            try{
                val result = profileRepository.getLatest()
                result.onSuccess {
                    _height.value = it.height.toString()
                    _weight.value = it.weight.toString()
                    _bmi.value = it.bmi.toString()
                }
            }
            finally {
            }
        }
    }

    fun loadWaterCups(){
        viewModelScope.launch {
            try {
                val result = waterIntakeRepository.getTodayWaterIntake()
                result.onSuccess {
                    _cups.value = it.cups
                }.onFailure {
                    _error.value = it.message
                }
            }
            catch (e: Exception){
                _error.value = e.message
            }
        }
    }
    private val maxCups = 8

    private var saveJob: Job? = null
    private val debounceDelay = 3000L

    fun increaseCup() {
        val current = _cups.value ?: 0
        if (current < maxCups) {
            _cups.value = current + 1
            debounceSave(current + 1)
        }
    }

    fun decreaseCup() {
        val current = _cups.value ?: 0
        if (current > 0) {
            _cups.value = current - 1
            debounceSave(current - 1)
        }
    }

    private fun debounceSave(cups: Int) {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(debounceDelay)
            saveCupsToDb(cups)
        }
    }

    private suspend fun saveCupsToDb(cups: Int) {
        val result = waterIntakeRepository.updateWaterCups(cups)
        result.onFailure {
            _error.postValue(it.message)
        }
    }
}