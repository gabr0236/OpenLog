package com.example.openlog.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.openlog.data.dao.CategoryDao
import com.example.openlog.data.entity.Log
import com.example.openlog.data.dao.LogDao
import com.example.openlog.data.entity.Category
import com.example.openlog.data.entity.CategoryWithLogs
import kotlinx.coroutines.launch
import java.util.*

class LogViewModel(
    private val categoryDao: CategoryDao,
    private val logDao: LogDao
) : ViewModel() {
    val allCategoryWithLogs: LiveData<List<CategoryWithLogs>> = categoryDao.getCategoriesWithLogs().asLiveData()
    val allCategories: LiveData<List<Category>> = categoryDao.getCategories().asLiveData()
    val allLogs: LiveData<List<Log>> = logDao.getLogs().asLiveData()

    private fun insertLog(log: Log) {
        viewModelScope.launch {
            logDao.insert(log)
        }
    }

    private fun getNewLogEntry(
        value: String
    ) : Log {
        return Log(
            value = value.toInt(),
            categoryOwnerId = 1,
            date = getCurrentDateTime()
        )
    }

    fun addNewLogEntry(
        value: String
    ) {
        val newLog = getNewLogEntry(value)
        insertLog(newLog)
    }

    fun isLogEntryValid(
        value: String
    ) : Boolean {
        if (value.isBlank()) {
            return false
        }
        return true
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}