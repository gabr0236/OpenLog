package com.example.openlog.models


data class Log(
    var time: String,
    var logCategory: LogCategory,
    var value: Double
) {
}