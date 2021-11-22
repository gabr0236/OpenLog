package com.example.openlog

import android.app.Application
import com.example.openlog.data.LogItemDatabase

class LogItemApplication : Application() {
    val database: LogItemDatabase by lazy {
        LogItemDatabase.getDatabase(this)
    }
}