package com.example.openlog.viewmodel

import androidx.lifecycle.*
import com.example.openlog.data.dao.LogCategoryDao
import com.example.openlog.data.dao.LogItemDao
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogCategoryWithLogItems
import com.example.openlog.data.entity.LogItem
import com.example.openlog.data.entity.LogItemAndLogCategory
import com.example.openlog.util.Statistics
import com.example.openlog.util.Statistics.Companion.round
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*

class SharedViewModel(
    private val logItemDao: LogItemDao,
    private val logCategoryDao: LogCategoryDao
) : ViewModel() {

    val allLogCategories: LiveData<List<LogCategory>> = logCategoryDao.getLogCategories().asLiveData()
    val allLogItems: LiveData<List<LogItem>> = logItemDao.getLogItems().asLiveData()

    private val _selectedCategory = MutableLiveData<LogCategory>()
    val selectedCategory: LiveData<LogCategory> = _selectedCategory

    private val _selectedLogItemToEdit = MutableLiveData<LogItem>()
    val selectedLogItemToEdit: LiveData<LogItem> = _selectedLogItemToEdit

    fun setSelectedLogItemToEdit(logItem: LogItem) {
        _selectedLogItemToEdit.value = logItem
    }

    fun setCategory(logCategory: LogCategory): Boolean {
        return if (selectedCategory.value != logCategory) {
            _selectedCategory.value?.isSelected = false
            _selectedCategory.value = logCategory
            _selectedCategory.value?.isSelected = true
            true
        } else false
    }

    fun updateLogItem(id: Int, value: String, date: Date?) {
        val updatedLogItem = getUpdatedLogItem(
            id,
            selectedCategory.value?.name.toString(),
            value,
            date
        )
        viewModelScope.launch {
            logItemDao.update(updatedLogItem)
        }
    }

    fun addNewLogItem(value: String, date: Date?) {
        val newLog = getNewLogItem(selectedCategory.value?.name.toString(), value, date)
        viewModelScope.launch {
            logItemDao.insert(newLog)
        }
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
        value: String,
        date: Date?
    ): LogItem {
        return LogItem(
            categoryOwnerName = category,
            value = value.toInt(),
            date = date ?: getCurrentDateTime()
        )
    }

    fun retrieveItem(id: Int): LiveData<LogItem> {
        return logItemDao.getLogItem(id).asLiveData()
    }

    //TODO: nok smartere bare at selecte fra de categorier vi allerede har hentet
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

    private val quantityOfLogsForDerivingStatistics =
        20 //Only derive average and standard deviation from n LogItems

    val mean: LiveData<Double> = MediatorLiveData<Double>()
        .apply {
            fun update() {
                value = logValues()?.average()?.round(2)
            }
            addSource(selectedCategory) { update() }
            update()
        }

    val standdarddeviation: LiveData<Double> = MediatorLiveData<Double>()
        .apply {
            fun update() {
                value = logValues()?.let { Statistics.standardDeviation(it) }?.round(2)
            }
            addSource(selectedCategory) { update() }
            update()
        }

    /**
     * @return the values of the LogItems where category equals selectedCategoryStatistics
     */
    fun logValues(): List<Int>? {
        return allLogItems.value?.asSequence()
            ?.filter { log -> log.categoryOwnerName == selectedCategory.value?.name }
            ?.take(quantityOfLogsForDerivingStatistics)
            ?.map { it.value }?.toList()
    }

    /**
     * @return the values and dates of the LogItems where category equals selectedCategoryStatistics
     */
    fun logValuesAndDates(): List<Pair<Int, Date?>>? {
        return allLogItems.value?.asSequence()
            ?.filter { log -> log.categoryOwnerName == selectedCategory.value?.name }
            ?.take(quantityOfLogsForDerivingStatistics)
            ?.map { Pair(it.value, it.date) }?.toMutableList()
    }

    fun createCategory(name: String, unit: String) {
        val newCategory = LogCategory(name, unit)
        viewModelScope.launch {
            logCategoryDao.insert(newCategory)
        }
    }
}