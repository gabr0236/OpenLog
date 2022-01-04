package com.example.openlog.viewmodel

import android.content.Intent
import android.net.Uri
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

    suspend fun retrieveAllItemsAndCategories(): List<LogCategoryWithLogItems> {
        return logCategoryDao.getLogCategoriesWithLogItems()
    }

    // TODO: Update to https://developer.android.com/training/data-storage/app-specific
    fun exportToCSV(logItemsAndLogCategory: List<LogCategoryWithLogItems>) : File {
        val SEPERATOR = ","
        val logFile = File.createTempFile("log_items", "csv")

        var fileWriter = FileWriter("log_items.csv")


       /* val bufferedWriter = BufferedWriter(
            OutputStreamWriter(
                FileOutputStream("log_items.csv"),
                "UTF-8"
            )
        )*/


        logItemsAndLogCategory.forEach {
            val line = StringBuffer()
            fileWriter.append(it.logCategory.name.toString())
            fileWriter.append(SEPERATOR)
            fileWriter.append(it.logCategory.unit.toString())
            fileWriter.append(SEPERATOR)

            it.logItems.forEach{
            fileWriter.append(it.id.toString())
            fileWriter.append(SEPERATOR)
            fileWriter.append(it.value.toString())
            fileWriter.append(SEPERATOR)
            fileWriter.append(it.date.toString())
         }
            fileWriter.write(line.toString())
            fileWriter.write(System.getProperty("line seperator"))
        }


        fileWriter.flush()
        fileWriter.close()


        return logFile
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

    fun createCategory(name: String, unit: String, emojiId: Int) {
        val newCategory = LogCategory(name, unit, emojiId)
        viewModelScope.launch {
            logCategoryDao.insert(newCategory)
        }
    }
}
