package com.example.openlog.util

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatter {
    companion object {
        private val sdf = SimpleDateFormat("yyyy EEEE dd/M hh:mm")
        fun formatDateTime(date: Date): String { return sdf.format(date) }
    }
}