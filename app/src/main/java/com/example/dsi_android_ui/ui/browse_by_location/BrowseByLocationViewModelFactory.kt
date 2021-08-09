package com.example.dsi_android_ui.ui.browse_by_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BrowseByLocationViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowseByLocationViewModel::class.java)) {
            return BrowseByLocationViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}