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
import java.io.*
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

    /**
     * Sets the selected category
     */
    fun setSelectedCategory(logCategory: LogCategory): Boolean {
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
            value.toFloat(),
            date
        )
        viewModelScope.launch {
            logItemDao.update(updatedLogItem)
        }
    }

    fun addNewLogItem(value: String, date: Date?) {
        val newLog = getNewLogItem(selectedCategory.value?.name.toString(), value.toFloat(), date)
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

    /**
     * creates and returns a log which is to be updated
     */
    private fun getUpdatedLogItem(
        id: Int,
        category: String,
        value: Float,
        date: Date?
    ): LogItem {
        return LogItem(
            id = id,
            categoryOwnerName = category,
            value = value,
            date = date ?: getCurrentDateTime()
        )
    }

    /**
     * Creates and returns new log
     */
    private fun getNewLogItem(
        category: String,
        value: Float,
        date: Date?
    ): LogItem {
        return LogItem(
            categoryOwnerName = category,
            value = value,
            date = date ?: getCurrentDateTime()
        )
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

     fun retrieveAllItemsAndCategories(): List<LogCategoryWithLogItems> {
        return logCategoryDao.getLogCategoriesWithLogItems()
    }


    fun exportToCSV(file : File) {
        val SEPERATOR = ","

        var fileWriter = FileWriter(file)


        fileWriter.append("Category")
        fileWriter.append(SEPERATOR)
        fileWriter.append("Unit")
        fileWriter.append(SEPERATOR)
        fileWriter.append("ValueID")
        fileWriter.append(SEPERATOR)
        fileWriter.append("Value")
        fileWriter.append(SEPERATOR)
        fileWriter.append("Date")
        fileWriter.append(SEPERATOR)
        fileWriter.appendLine()

        retrieveAllItemsAndCategories().forEach { category ->

            category.logItems.forEach{ log ->
                fileWriter.append(category.logCategory.name.toString())
                fileWriter.append(SEPERATOR)
                fileWriter.append(category.logCategory.unit.toString())
                fileWriter.append(SEPERATOR)
                fileWriter.append(log.id.toString())
                fileWriter.append(SEPERATOR)
                fileWriter.append(log.value.toString())
                fileWriter.append(SEPERATOR)
                fileWriter.append(log.date.toString())
                fileWriter.appendLine()
         }
            fileWriter.appendLine()
        }
        fileWriter.flush()
        fileWriter.close()

    }

    private val quantityOfLogsForDerivingStatistics =
        20 //Only derive average and standard deviation from n LogItems

    fun mean(): Double? = logValues()?.average()?.round(2)
    fun standdarddeviation(): Double? = logValues()?.let { Statistics.standardDeviation(it) }?.round(2)


    /**
     * @return the values of the LogItems where category equals selectedCategoryStatistics
     */
    fun logValues(): List<Float>? {
        return allLogItems.value?.asSequence()
            ?.filter { log -> log.categoryOwnerName == selectedCategory.value?.name }
            ?.take(quantityOfLogsForDerivingStatistics)
            ?.map { it.value }?.toList()
    }

    /**
     * @return the values and dates of the LogItems where category equals selectedCategoryStatistics
     */
    fun logValuesAndDates(): List<Pair<Float, Date?>>? {
        return allLogItems.value?.asSequence()
            ?.filter { log -> log.categoryOwnerName == selectedCategory.value?.name }
            ?.take(quantityOfLogsForDerivingStatistics)
            ?.map { Pair(it.value, it.date) }?.toMutableList()
    }

    /**
     * creates a new category using params
     */
    fun createCategory(name: String, unit: String, emojiId: Int) {
        val newCategory = LogCategory(name, unit, emojiId)
        viewModelScope.launch {
            logCategoryDao.insert(newCategory)
        }
    }

    /**
     * @return all the logs of the selected category
     */
    fun logsOfSelectedCategory(): List<LogItem>? {
        return allLogItems.value?.asSequence()
            ?.filter { log -> log.categoryOwnerName == selectedCategory.value?.name}
            ?.toList()
    }

    /**
     * @return whether any logs for the selected category exists
     */
    fun anyLogsOfSelectedCategory(): Boolean? {
        return allLogItems.value?.asSequence()
            ?.filter { log -> log.categoryOwnerName == selectedCategory.value?.name}
            ?.any()
    }

    fun deleteCategory(logCategory: LogCategory){
        viewModelScope.launch {
            logCategoryDao.delete(logCategory)
        }
    }


}
