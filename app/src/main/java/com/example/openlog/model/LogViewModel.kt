package com.example.openlog.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.openlog.constants.Category
import com.example.openlog.data.Log
import com.example.openlog.data.LogDao
import kotlinx.coroutines.launch
import java.util.*

class LogViewModel(private val logDao: LogDao) : ViewModel() {
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