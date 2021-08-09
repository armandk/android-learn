package com.example.dsi_android_ui.ui.add_task

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.dsi_android_ui.network.VolleyConsumer
import com.example.dsi_android_ui.utils.TASK_SERVICE_URL
import org.json.JSONObject
import java.nio.charset.Charset


class AddNewTaskRepository() {

    private val _tag = "ADD_TASK_REPOSITORY"

    fun createTask(payload: JSONObject, listener: VolleyRequestListener) {
        Log.d(_tag, "Creating Task")
        Log.d(_tag, "Payload: $payload")
        val request = JsonObjectRequest(
            Request.Method.POST,
            TASK_SERVICE_URL,
            payload,
            { listener.onResponse(it.toString()) },
            { it.message?.let { message -> listener.onError(message) } })

        VolleyConsumer.addRequest(request)
    }

    fun updateTask(payload: JSONObject, listener: VolleyRequestListener) {
        Log.d(_tag, "Updating Task")
        val url = "$TASK_SERVICE_URL/${payload["id"]}"
        Log.d(_tag, "URL: $url")
        Log.d(_tag, "Payload: $payload")

        val putRequest: StringRequest = object : StringRequest(
            Method.PUT, url,
            { listener.onResponse(it) },
            { it.message?.let { message -> listener.onError(message) } }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }

            override fun getBody(): ByteArray? {
                return try {
                    payload.toString().toByteArray(Charset.forName("UTF-8"))
                } catch (e: IllegalArgumentException) {
                    Log.d(_tag, "ERROR: ${e.message}")
                    null
                }
            }
        }
        VolleyConsumer.addRequest(putRequest)
    }

}