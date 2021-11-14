package com.example.openlog.models

data class LogCategory (
    val logType: LogType,
    val logUnit: LogUnit){
    fun getTypeString(): String {
        return when (logType){
            LogType.INSULIN -> "Insulin"
            LogType.BLOOD_SUGAR -> "Blodsukker"
            LogType.CALORIES -> "Kalorier"
        }
    }

    fun getUnitString(): String {
        return when (logUnit){
            LogUnit.UNITS -> "Enheder"
            LogUnit.KCAL -> "KCal"
            LogUnit.MILLI_MOLES_PER_LITRE -> "mmol/l"
        }
    }
}