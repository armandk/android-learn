package com.example.dsi_android_ui;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.dsi_android_ui.network.VolleyConsumer;

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        VolleyConsumer.setQueue(volleyRequestQueue);
    }
}