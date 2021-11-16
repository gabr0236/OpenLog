package com.example.openlog

import android.app.Application
import com.example.openlog.data.AppDatabase

class LogApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}