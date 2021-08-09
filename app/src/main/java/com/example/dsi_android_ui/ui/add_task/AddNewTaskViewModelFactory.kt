package com.example.dsi_android_ui.ui.add_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddNewTaskViewModelFactory :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddNewTaskViewModel::class.java) -> AddNewTaskViewModel(AddNewTaskRepository()) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

