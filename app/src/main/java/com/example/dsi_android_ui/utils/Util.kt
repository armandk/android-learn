package com.example.dsi_android_ui.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray
import org.json.JSONObject

object Util {

    var gson: Gson

    init {
        gson = Gson()
    }

    fun getJsonString(any: Any): String =  gson.toJson(any)

    fun getJsonObject(any: Any) = JSONObject(getJsonString(any))

    fun getJsonArray(any: Any) = JSONArray(getJsonString(any))

}