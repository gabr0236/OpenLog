package com.example.openlog.data.entity

import androidx.room.*
import java.util.*


@Entity(tableName = "log_item")
data class LogItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_item_id")
    val id: Int = 0,
    @ColumnInfo(name = "log_category_owner_name")
    val categoryOwnerName: String,
    @ColumnInfo(name = "log_item_value")
    val value: Float,
    @ColumnInfo(name = "log_item_date")
    val date: Date?
)

@Entity(tableName = "log_item_and_log_category")
data class LogItemAndLogCategory(
    @Embedded
    val logItem: LogItem,
    @Relation(
        parentColumn = "log_category_owner_name",
        entityColumn = "log_category_name"
    )
    val logCategory: LogCategory
)