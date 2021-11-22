package com.example.openlog.util

import kotlin.math.pow
import kotlin.math.sqrt

class Statistics {
    fun calculateMean(values: List<Int>): Double {
        return values.average()
    }

    fun calculateVariance(values: List<Int>): Double {
        val mean = calculateMean(values)

        var sum = 0.0

        values.forEach {
            sum += (it.toDouble() - mean).pow(2.0)
        }

        return ((1 / values.size).toDouble()) * sum
    }

    fun calculateStandardDeviation(values: List<Int>): Double {
        return sqrt(calculateVariance(values))
    }
}