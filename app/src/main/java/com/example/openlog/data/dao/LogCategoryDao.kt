package com.example.openlog.data.dao

import androidx.room.*
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogCategoryWithLogItems
import kotlinx.coroutines.flow.Flow

@Dao
interface LogCategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(logCategory: LogCategory)

    @Update
    suspend fun update(logCategory: LogCategory)

    @Query("SELECT * FROM log_category WHERE log_category_name = :name")
    fun getLogCategory(name: String): Flow<LogCategory>

    @Transaction
    @Query("SELECT * FROM log_category WHERE log_category_name = :name")
    fun getLogCategoryWithLogItems(name: String): Flow<LogCategoryWithLogItems>

    @Query("SELECT * FROM log_category")
    fun getLogCategories(): Flow<List<LogCategory>>

    @Transaction
    @Query("SELECT * FROM log_category")
    fun getLogCategoriesWithLogItems(): List<LogCategoryWithLogItems>

    // Only names
    @Query("SELECT log_category_name FROM log_category")
    fun getLogCategoryNames(): Flow<List<String>>
}