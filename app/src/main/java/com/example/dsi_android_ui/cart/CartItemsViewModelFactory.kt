package com.example.dsi_android_ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CartItemsViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CartItemsViewModel() as T
    }
}