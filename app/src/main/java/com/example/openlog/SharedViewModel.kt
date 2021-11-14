package com.example.openlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.openlog.models.LogCategory
import com.example.openlog.models.Log
import com.example.openlog.models.LogType
import com.example.openlog.models.LogUnit


class SharedViewModel : ViewModel() {
    private val _logs = MutableLiveData<MutableList<Log>>()
    val logs: LiveData<MutableList<Log>> = _logs


    companion object {
        val categories = arrayOf(
            LogCategory(LogType.BLOOD_SUGAR,LogUnit.MILLI_MOLES_PER_LITRE),
            LogCategory(LogType.CALORIES,LogUnit.KCAL),
            LogCategory(LogType.INSULIN,LogUnit.UNITS)
        )
    }
}