package com.example.dsi_android_ui.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

/*
 * @ author: Luis Esparza
 */
public class VolleyConsumer {
    @Setter
    private static RequestQueue queue;

    /*
     * Fast convenience method to consume GET request from anywhere on the app.
     * This provides easy asynchronous url consumption
     *
     * void sendRequest(){
     *   VolleyConsumer.get("http://someUrl",this::responseHandler)
     * }
     * void responseHandler(String response){
     *   //...handle your response here
     * }
     *
     * @param url the full URL to be consumed
     * @param listener preferably a method to receive the response
     */
    public static void get(String url, Response.Listener<String> listener) {
        queue.add(getStringRequest(url, listener));
    }

    public static void getPut(String url, Response.Listener<String> listener) {
        queue.add(getStringPutRequest(url, listener));
    }

    @NonNull
    private static StringRequest getStringRequest(String url, Response.Listener<String> listener) {
        return new StringRequest(url, listener,
                error -> {
                    error.printStackTrace();
                    logError(error.getMessage());
                    listener.onResponse("");
                });
    }

    private static StringRequest getStringPutRequest(String url, Response.Listener<String> listener) {
        return new StringRequest(Request.Method.PUT, url, listener,
                error -> {
                    error.printStackTrace();
                    logError(error.getMessage());
                    listener.onResponse("");
                });
    }

    public static void getDelete(String url, Response.Listener<String> listener) {
        queue.add(getStringDeleteRequest(url, listener));
    }

    private static StringRequest getStringDeleteRequest(String url, Response.Listener<String> listener) {
        return new StringRequest(Request.Method.DELETE, url, listener,
                error -> {
                    error.printStackTrace();
                    logError(error.getMessage());
                    listener.onResponse("");
                });
    }

    private static void logError(String error) {
        Log.e("FastVolley", "" + error);
    }

    public static void get(JsonObjectRequest request) {
        queue.add(request);
    }
    public static void get(JsonArrayRequest request) {
        queue.add(request);
    }

    public static void addRequest(Request request) {
        queue.add(request);
    }

}
