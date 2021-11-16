package com.example.openlog.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.openlog.data.dao.CategoryDao
import com.example.openlog.data.dao.LogDao

class LogViewModelFactory(
    private val categoryDao: CategoryDao,
    private val logDao: LogDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogViewModel(categoryDao, logDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}