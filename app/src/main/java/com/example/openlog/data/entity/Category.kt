package com.example.openlog.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    val categoryId: Int = 0,
    @ColumnInfo(name = "category_name")
    val name: String,
    @ColumnInfo(name = "category_unit")
    val unit: String?
)