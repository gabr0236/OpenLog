package com.example.openlog.viewmodel

import androidx.lifecycle.*
import androidx.paging.*
import com.example.openlog.data.dao.LogCategoryDao
import com.example.openlog.data.dao.LogItemDao
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.data.entity.LogItemAndLogCategory
import com.example.openlog.util.Statistics
import com.example.openlog.util.Statistics.Companion.round
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList

class SharedViewModel(
    private val logItemDao: LogItemDao,
    private val logCategoryDao: LogCategoryDao
) : ViewModel() {

    val allLogCategories: LiveData<List<LogCategory>> = logCategoryDao.getLogCategories().asLiveData()
    val logItems = Pager(PagingConfig(
        pageSize = 10,
        enablePlaceholders = false,
        maxSize = 30,
        prefetchDistance = 10,
        initialLoadSize = 10
    )){
        logItemDao.getLogsByCategoryPaged(selectedCategory.value?.name)
    }.flow

    private val _selectedCategory = MutableLiveData<LogCategory>()
    val selectedCategory: LiveData<LogCategory> = _selectedCategory

    private val _selectedLogItemToEdit = MutableLiveData<LogItem>()
    val selectedLogItemToEdit: LiveData<LogItem> = _selectedLogItemToEdit

    private lateinit var lastSnapshotLogItems: ItemSnapshotList<LogItem>

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
    // TODO this number should be equal to the amount of loaded logs when load is implemented

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
