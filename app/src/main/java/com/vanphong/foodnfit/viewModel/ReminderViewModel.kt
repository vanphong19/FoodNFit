package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.Reminder

class ReminderViewModel: ViewModel() {
    private val _reminderList = MutableLiveData<List<Reminder>>()
    val reminderList: LiveData<List<Reminder>> get() = _reminderList
    fun setReminderList(data: List<Reminder>){
        _reminderList.value = data
    }
}