package com.example.openlog.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.openlog.data.entity.Category
import com.example.openlog.data.entity.CategoryWithLogs
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT category_id, category_name FROM category")
    fun getCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM category")
    fun getCategoriesWithLogs(): Flow<List<CategoryWithLogs>>
}