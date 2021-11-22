package com.example.openlog

import androidx.room.*
import com.example.openlog.data.entity.LogItem

@Entity(tableName = "log_category")
data class LogCategory(
    @PrimaryKey
    @ColumnInfo(name = "log_category_name")
    val name: String,
    @ColumnInfo(name = "log_category_unit")
    val unit: String
)

@Entity(tableName = "log_category_with_log_items")
data class LogCategoryWithLogItems(
    @Embedded
    val logCategory: LogCategory,
    @Relation(
        parentColumn = "log_category_name",
        entityColumn = "log_category_owner_name"
    )
    val logItems: List<LogItem>
)