package com.example.dsi_android_ui;

import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.util.Log;

public class SearchHistory extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.example.dsi_android_ui.SearchHistory";
    public SearchHistory(){
        setupSuggestions(AUTHORITY,DATABASE_MODE_QUERIES);
    }

    @Override
    public boolean onCreate() {
        Log.i("SearchHistory","Authority: " + AUTHORITY);
        return super.onCreate();
    }
}
