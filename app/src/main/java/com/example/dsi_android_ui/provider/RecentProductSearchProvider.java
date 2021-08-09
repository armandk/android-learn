package com.example.dsi_android_ui.provider;

import android.content.SearchRecentSuggestionsProvider;

public class RecentProductSearchProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.example.dsi_android_ui.provider.SearchRecentSuggestionsProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;
    public RecentProductSearchProvider(){
        setupSuggestions(AUTHORITY, MODE);
    }
}
