package com.example.openlog.data

import androidx.room.TypeConverter
import com.example.openlog.constants.Category
import java.util.*

// https://developer.android.com/training/data-storage/room/referencing-data
class LogConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    /*
    @TypeConverter
    fun fromCategory(value: Category?): Int? {
        return value?.ordinal
    }

    @TypeConverter
    fun intToCategory(value: Int?): Category? {
        return enumValues<Category>()[value!!]
    }
     */
}