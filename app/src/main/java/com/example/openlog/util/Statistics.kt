package com.example.openlog.util

import kotlin.math.pow

class Statistics {
    companion object {
        fun standardDeviation(integerList: List<Int>): Double {
            val mean = integerList.average()
            return integerList
                .fold(0.0, { accumulator, next -> accumulator + (next - mean).pow(2.0) })
                .let { kotlin.math.sqrt(it / integerList.size) }
        }

        fun Double.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return kotlin.math.round(this * multiplier) / multiplier
        }
    }
}