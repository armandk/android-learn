package com.example.dsi_android_ui.service

import org.json.JSONObject

interface  CallbackService {

    fun success(it: JSONObject)

    fun error(message: String);

    fun loading();
}