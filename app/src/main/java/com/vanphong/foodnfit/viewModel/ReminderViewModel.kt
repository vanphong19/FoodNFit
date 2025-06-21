package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.Reminder
import com.vanphong.foodnfit.model.ReminderResponse
import com.vanphong.foodnfit.repository.ReminderRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class ReminderViewModel: ViewModel() {
    private val repository = ReminderRepository()
    private val _reminderList = MutableLiveData<List<ReminderResponse>>()
    val reminderList: LiveData<List<ReminderResponse>> get() = _reminderList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message
    private val _type = MutableLiveData<String>()
    val type: LiveData<String> get() = _type
    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time
    fun setMessage(value: String) {
        _message.value = value
    }

    fun setType(value: String) {
        _type.value = value
    }

    fun setTime(value: String) {
        _time.value = value
    }

    fun getAllReminder(){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getAll()
                result.onSuccess {
                    _reminderList.value = it
                }.onFailure {
                    _error.value = "Không thể lấy dữ liệu"
                }
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun getById(id: Int){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getById(id)
                result.onSuccess {
                    _type.value = it.reminderType
                    _time.value = formatDateTime(it.scheduledTime)
                    val cleanedMessage = it.message
                        .replace("**", "") // Bỏ in đậm markdown
                        .replace("* ", "• ") // Thay `*` đầu dòng thành chấm tròn
                        .replace("*", "")
                    _message.value = cleanedMessage
                }.onFailure {
                    _error.value = "Không thể lấy dữ liệu"
                }
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatDateTime(input: String): String {
        return try {
            val inputFormatter = DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .toFormatter()

            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
            val dateTime = LocalDateTime.parse(input, inputFormatter)
            dateTime.format(outputFormatter)
        } catch (e: Exception) {
            input
        }
    }
}