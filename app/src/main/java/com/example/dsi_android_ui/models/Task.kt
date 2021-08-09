package com.example.dsi_android_ui.models

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

data class Task(
    var typeId: Int? = null,
    var description: String? = null,
    var id: Long? = null,
    var statusId: Int = 1,
    var dueTimeId: Int = 3,
    var userId: Int = 0,
    var priorityId: Int? = null,
    var dueDate: String = df.format(Date()),
    var active: Int = 1,
    var products: List<ProductInTask>? = listOf()
) {

    fun getDueDateAsDate(): Date? {
        return dueDate.let { df.parse(it) }
    }

    fun setDueDate(date: Date) {
        dueDate = df.format(date)
    }

    companion object {
        private val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        fun parseToJsonString(task: Task): String {
            return Gson().toJson(task)
        }

        fun parseToTask(jsonString: String): Task? {
            return Gson().fromJson(jsonString, Task::class.java)
        }
    }
}