package com.example.openlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.openlog.data.dao.LogCategoryDao
import com.example.openlog.data.dao.LogItemDao

class LogItemViewModelFactory(
    private val logItemDao: LogItemDao,
    private val logCategoryDao: LogCategoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogItemViewModel(logItemDao, logCategoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}