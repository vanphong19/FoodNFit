package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.FeedbackRequest
import com.vanphong.foodnfit.model.FeedbackResponse
import com.vanphong.foodnfit.model.ReminderResponse
import com.vanphong.foodnfit.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FeedbackViewModel: ViewModel() {
    private val feedbackRepository = FeedbackRepository()
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message
    private val _purpose = MutableLiveData<String>()
    val purpose: LiveData<String> get() = _purpose
    private val _inquiry = MutableLiveData<String>()
    val inquiry: LiveData<String> get() = _inquiry
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> get() = _imageUrl
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _createResult = MutableLiveData<FeedbackResponse>()
    val createResult: LiveData<FeedbackResponse> get() = _createResult

    fun setMessage(value: String) {
        _message.value = value
    }

    fun setPurpose(value: String) {
        _purpose.value = value
    }

    fun setInquiry(value: String) {
        _inquiry.value = value
    }

    fun setImageUrl(value: String?) {
        _imageUrl.value = value
    }


    fun createFeedback(request: FeedbackRequest){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = feedbackRepository.createFeedback(request)
                result.onSuccess {
                    _createResult.value = it
                }.onFailure {
                    _error.value = it.message
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
                val response = feedbackRepository.uploadImageFromFile(file)
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
}