package com.example.openlog.data.entity

import androidx.room.*


@Entity(tableName = "log_category")
data class LogCategory(
    @PrimaryKey
    @ColumnInfo(name = "log_category_name")
    val name: String,
    @ColumnInfo(name = "log_category_unit")
    val unit: String
) {
    @Ignore
    var isSelected = false
}

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