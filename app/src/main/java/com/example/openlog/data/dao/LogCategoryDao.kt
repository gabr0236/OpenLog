package com.example.openlog.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.openlog.LogCategory
import com.example.openlog.LogCategoryWithLogItems
import kotlinx.coroutines.flow.Flow

@Dao
interface LogCategoryDao {
    @Query("SELECT * FROM log_category WHERE log_category_name = :name")
    fun getLogCategory(name: String): Flow<LogCategory>

    @Transaction
    @Query("SELECT * FROM log_category WHERE log_category_name = :name")
    fun getLogCategoryWithLogItems(name: String): Flow<LogCategoryWithLogItems>

    @Query("SELECT * FROM log_category")
    fun getLogCategories(): Flow<List<LogCategory>>

    @Transaction
    @Query("SELECT * FROM log_category")
    fun getLogCategoriesWithLogItems(): Flow<List<LogCategoryWithLogItems>>

    // Only names
    @Query("SELECT log_category_name FROM log_category")
    fun getLogCategoryNames(): Flow<List<String>>
}