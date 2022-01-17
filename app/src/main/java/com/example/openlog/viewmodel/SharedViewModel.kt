package com.example.openlog.viewmodel

import androidx.lifecycle.*
import androidx.paging.*
import com.example.openlog.data.dao.LogCategoryDao
import com.example.openlog.data.dao.LogItemDao
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogCategoryWithLogItems
import com.example.openlog.data.entity.LogItem
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
    val logItems = Pager(PagingConfig(
        pageSize = 30,
        enablePlaceholders = true,
        maxSize = 120,
    )){
        logItemDao.getLogsByCategoryPaged(selectedCategory.value?.name)
    }.flow

    private val _selectedCategory = MutableLiveData<LogCategory>()
    val selectedCategory: LiveData<LogCategory> = _selectedCategory

    private val _selectedLogItemToEdit = MutableLiveData<LogItem>()
    val selectedLogItemToEdit: LiveData<LogItem> = _selectedLogItemToEdit

    private lateinit var lastSnapshotLogItems: ItemSnapshotList<LogItem>

    private val quantityOfLogsForDerivingStatistics =
        30 //Only derive average and standard deviation from n LogItems

    //Snapshot is set in PreviousLogsFragment after the and during when
    // the LogItemPagingAdapter is being populated and updated
    fun setLastSnapshotLogItems(logItems: ItemSnapshotList<LogItem>){
         lastSnapshotLogItems = logItems
    }

    fun setSelectedLogItemToEdit(logItem: LogItem?) {
        logItem?: return
        _selectedLogItemToEdit.value = logItem!!
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

     private fun retrieveAllItemsAndCategories(): List<LogCategoryWithLogItems> {
        return logCategoryDao.getLogCategoriesWithLogItems()
    }


    /**
     * Translates database into csv format and writes this in the file given as param
     */
    fun exportToCSV(file : File) {
        val SEPERATOR = ","

        val fileWriter = FileWriter(file)

// Writes out the order of log values in file
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
// writes category/log values in file
            category.logItems.forEach{ log ->
                fileWriter.append(category.logCategory.name)
                fileWriter.append(SEPERATOR)
                fileWriter.append(category.logCategory.unit)
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

    fun mean(): Double = logValues().average().round(2)
    fun standdarddeviation(): Double = logValues().let { Statistics.standardDeviation(it) }.round(2)


    /**
     * @return the values of the LogItems where category equals selectedCategoryStatistics
     */
    private fun logValues(): List<Float> {
        return lastSnapshotLogItems
            .take(quantityOfLogsForDerivingStatistics)
            .mapNotNull { it }
            .map { it.value }.toList()
    }

    /**
     * @return the values and dates of the LogItems where category equals selectedCategoryStatistics
     */
    fun logValuesAndDates(): List<Pair<Float?, Date?>> {
        return lastSnapshotLogItems
            .take(quantityOfLogsForDerivingStatistics)
            .map { Pair(it?.value, it?.date) }.toMutableList()
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

    fun deleteCategory(logCategory: LogCategory){
        viewModelScope.launch {
            logCategoryDao.delete(logCategory)
        }
    }
}
