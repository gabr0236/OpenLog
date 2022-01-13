package com.example.openlog.data.dao


import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.openlog.data.entity.LogItem
import com.example.openlog.data.entity.LogItemAndLogCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface LogItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(logItem: LogItem)

    @Update
    suspend fun update(logItem: LogItem)

    @Delete
    suspend fun delete(logItem: LogItem)

    @Query("SELECT * FROM log_item WHERE log_item_id = :logItemId")
    fun getLogItem(logItemId: Int): Flow<LogItem>

    @Query("SELECT * FROM log_item WHERE log_item_id = :logItemId")
    fun getFullLogItem(logItemId: Int): Flow<List<LogItemAndLogCategory>>

    //@Query("SELECT * from log_item ORDER BY log_item_date ASC")
    //fun getLogItems(): Flow<List<LogItem>>

    //@Query("SELECT * FROM log_item ORDER BY log_item_date ASC")
    //fun getFullLogItems(): Flow<List<LogItemAndLogCategory>>

    @Query("SELECT * FROM log_item WHERE log_category_owner_name = :categoryName ORDER BY log_item_date ASC")
    fun getLogsByCategoryPaged(categoryName: String): PagingSource<Int,LogItem>
}