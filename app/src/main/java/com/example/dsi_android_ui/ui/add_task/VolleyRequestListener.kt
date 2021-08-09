package com.example.dsi_android_ui.ui.add_task

interface VolleyRequestListener {
    fun onError(message: String)
    fun onResponse(response: String)
}