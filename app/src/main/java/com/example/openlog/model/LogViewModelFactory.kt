package com.example.openlog.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.openlog.data.LogDao

class LogViewModelFactory(private val logDao: LogDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogViewModel(logDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}