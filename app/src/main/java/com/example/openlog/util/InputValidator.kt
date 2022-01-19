package com.example.openlog.util

import android.content.Context
import android.widget.Toast
import com.example.openlog.R

class InputValidator {
    companion object{
        /**
         * Best suited solution if negative and positive number which can be formatted with '-' and '.'
         */
        fun isValidNumber(context: Context,s: String?) : Boolean {
            val regex = """^(-)?[0-9]{0,}((\.){1}[0-9]{1,}){0,1}$""".toRegex()
            return if (s.isNullOrEmpty() || s.isBlank()) false
            else if (s.contains(",")){
                Toast.makeText(context, context.getString(R.string.dot_not_comma), Toast.LENGTH_LONG).show()
                false
            } else regex.matches(s)
        }
        fun isUnlikelyNumber(integerList: MutableList<Float>, value : Float) : Boolean{
            val mean = integerList.average()
            val sd = Statistics.standardDeviation(integerList)
            val conficendeIntervalLower = mean - (1.96 - integerList.size-1) * (sd / Math.sqrt(integerList.size.toDouble()))
            val conficendeIntervalHigher = mean + (1.96 - integerList.size-1) * (sd / Math.sqrt(integerList.size.toDouble()))
            return value < conficendeIntervalLower || value > conficendeIntervalHigher
        }
    }
}