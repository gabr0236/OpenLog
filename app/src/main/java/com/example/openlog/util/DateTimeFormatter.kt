package com.example.openlog.util

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatter {
    companion object {
        private val patternYearDayDateTime = SimpleDateFormat("yyyy EEEE dd/M hh:mm")
        private val patternDate = SimpleDateFormat("dd/M")

        fun formatAsYearDayDateTime(date: Date): String {
            return patternYearDayDateTime.format(date)
        }

        fun formatAsDate(date: Date): String {
            return patternDate.format(date)
        }
    }
}