package com.example.openlog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.openlog.constants.Category
import java.text.DateFormat
import java.util.*

@Entity(tableName = "log")
data class Log(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    /*
    @ColumnInfo(name = "category")
    val category: Int,
     */
    @ColumnInfo(name = "value")
    val value: Int,
    @ColumnInfo(name = "date")
    val date: Date?
)

fun Log.getFormattedDate(): String =
    DateFormat.getDateInstance().format(date)