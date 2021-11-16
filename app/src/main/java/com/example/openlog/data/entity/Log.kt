package com.example.openlog.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.*

@Entity(tableName = "log")
data class Log(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    val logId: Int = 0,
    @ColumnInfo(name = "category_owner_id")
    val categoryOwnerId: Int,
    @ColumnInfo(name = "log_value")
    val value: Int,
    @ColumnInfo(name = "log_date")
    val date: Date?
)

fun Log.getFormattedDate(): String =
    DateFormat.getDateInstance().format(date)