//package com.vanphong.foodnfit.viewModel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//
//class SetupViewModel : ViewModel() {
//
//    // Sử dụng LiveData để các Fragment/Activity có thể lắng nghe sự thay đổi
//    private val _userData = MutableLiveData<UserSetupData>(UserSetupData())
//    val userData: LiveData<UserSetupData> = _userData
//
//    fun setName(name: String) {
//        _userData.value?.name = name
//        // Dòng này không bắt buộc nhưng tốt cho việc thông báo cập nhật
//        _userData.value = _userData.value
//    }
//
//    fun setGender(gender: String) {
//        _userData.value?.gender = gender
//        _userData.value = _userData.value
//    }
//
//    fun setBirthDate(birthDate: String, age: Int) {
//        _userData.value?.let {
//            it.birthDate = birthDate
//            it.age = age
//        }
//        _userData.value = _userData.value
//    }
//
//    fun setHeight(height: Int) {
//        _userData.value?.height = height
//        _userData.value = _userData.value
//    }
//
//    fun setWeight(weight: Int) {
//        _userData.value?.weight = weight
//        _userData.value = _userData.value
//    }
//
//    fun setGoal(goal: String) {
//        _userData.value?.goal = goal
//        _userData.value = _userData.value
//    }
//
//    // Lấy dữ liệu hiện tại một cách an toàn
//    fun getCurrentData(): UserSetupData {
//        return _userData.value ?: UserSetupData()
//    }
//}