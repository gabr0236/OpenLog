package com.example.openlog

import android.app.Application
import com.example.openlog.data.LogRoomDatabase

class LogApplication : Application() {
    val database: LogRoomDatabase by lazy {
        LogRoomDatabase.getDatabase(this)
    }
}