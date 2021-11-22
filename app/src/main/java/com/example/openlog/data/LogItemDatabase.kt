package com.example.openlog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.openlog.LogCategory
import com.example.openlog.data.converter.DateConverter
import com.example.openlog.data.dao.LogCategoryDao
import com.example.openlog.data.dao.LogItemDao
import com.example.openlog.data.entity.LogItem

@Database(entities = [LogItem::class, LogCategory::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class LogItemDatabase : RoomDatabase() {
    abstract fun logItemDao(): LogItemDao
    abstract fun logCategoryDao(): LogCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: LogItemDatabase? = null

        fun getDatabase(context: Context): LogItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogItemDatabase::class.java,
                    "log_item_database"
                )
                    .createFromAsset("database/log_categories.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}