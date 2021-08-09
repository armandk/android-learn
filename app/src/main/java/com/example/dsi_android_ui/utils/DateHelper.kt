package com.example.dsi_android_ui.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class DateHelper {
    var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun convertToString(date: Date): String {
        return dateFormat.format(date)
    }

    fun convertToDate(date: String): Date? {
        return dateFormat.parse(date)
    }
}