package com.example.openlog.util

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatter {
    companion object {
        private val patternYearDayDateTime = SimpleDateFormat("EEEE dd/MM/yyyy hh:mm")
        private val patternDate = SimpleDateFormat("dd/MM")

        fun formatAsYearDayDateTime(date: Date): String {
            return patternYearDayDateTime.format(date)
        }

        fun formatAsDate(date: Date): String {
            return patternDate.format(date)
        }
    }
}