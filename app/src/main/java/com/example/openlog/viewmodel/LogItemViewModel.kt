package com.example.openlog.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.openlog.LogCategory
import com.example.openlog.LogCategoryWithLogItems
import com.example.openlog.data.dao.LogCategoryDao
import com.example.openlog.data.dao.LogItemDao
import com.example.openlog.data.entity.LogItem
import com.example.openlog.data.entity.LogItemAndLogCategory
import com.example.openlog.util.Statistics
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*

class LogItemViewModel(
    private val logItemDao: LogItemDao,
    private val logCategoryDao: LogCategoryDao
) : ViewModel() {

    val allLogCategories: LiveData<List<LogCategory>> = logCategoryDao.getLogCategories().asLiveData()
    val allLogCategoryNames: LiveData<List<String>> = logCategoryDao.getLogCategoryNames().asLiveData()
    val allLogItems: LiveData<List<LogItem>> = logItemDao.getLogItems().asLiveData()
    val allFullLogItems: LiveData<List<LogItemAndLogCategory>> = logItemDao.getFullLogItems().asLiveData()

    private val _selectedCategory = MutableLiveData<LogCategory>()
    val selectedCategory: LiveData<LogCategory> = _selectedCategory

    fun setCategory(logCategory: LogCategory): Boolean {
        return if (selectedCategory.value!=logCategory) {
            _selectedCategory.value?.isSelected = false
            _selectedCategory.value = logCategory
            logCategory.isSelected = true
            true
        } else false
    }

    private fun updateLogItem(logItem: LogItem) {
        viewModelScope.launch {
            logItemDao.update(logItem)
        }
    }

    fun updateLogItem(
        id: Int,
        // category: String,
        value: String,
        date: Date?
    ) {
        val updatedLogItem = getUpdatedLogItem(
            id,
            selectedCategory.value?.name.toString(),
            value,
            date
        )
        updateLogItem(updatedLogItem)
    }

    fun deleteLogItem(logItem: LogItem) {
        viewModelScope.launch {
            logItemDao.delete(logItem)
        }
    }

    fun shareLogItems() {
        TODO()
    }

    private fun getUpdatedLogItem(
        id: Int,
        category: String,
        value: String,
        date: Date?
    ): LogItem {
        return LogItem(
            id = id,
            categoryOwnerName = category,
            value = value.toInt(),
            date = date ?: getCurrentDateTime()
        )
    }

    private fun getNewLogItem(
        category: String,
        value: String
    ) : LogItem {
        return LogItem(
            categoryOwnerName = category,
            value = value.toInt(),
            date = getCurrentDateTime()
        )
    }

    fun addNewLogItem(value: String) {
        val newLog = getNewLogItem(selectedCategory.value?.name.toString(), value)
        viewModelScope.launch {
            logItemDao.insert(newLog)
        }
    }

    fun retrieveItem(id: Int): LiveData<LogItem> {
        return logItemDao.getLogItem(id).asLiveData()
    }

    fun retrieveItemsByCategory(name: String): LiveData<LogCategoryWithLogItems> {
        return logCategoryDao.getLogCategoryWithLogItems(name).asLiveData()
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    // TODO: Update to https://developer.android.com/training/data-storage/app-specific
    private fun exportToCSV(logItemsAndLogCategory: List<LogItemAndLogCategory>) {
        val SEPERATOR = ","

        val bufferedWriter = BufferedWriter(
            OutputStreamWriter(
                FileOutputStream("log_items.csv"),
                "UTF-8"
            )
        )

        logItemsAndLogCategory.forEach {
            val line = StringBuffer()
            line.append(it.logItem.id)
            line.append(SEPERATOR)
            line.append(it.logCategory.name)
            line.append(SEPERATOR)
            line.append(it.logItem.value)
            line.append(SEPERATOR)
            line.append(it.logCategory.unit)
            line.append(SEPERATOR)
            line.append(it.logItem.date)

            bufferedWriter.write(line.toString())
            bufferedWriter.newLine()
        }

        bufferedWriter.flush()
        bufferedWriter.close()
    }
}