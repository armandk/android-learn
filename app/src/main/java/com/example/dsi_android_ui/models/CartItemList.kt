package com.example.dsi_android_ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItemList(val cart_items: List<CartItem>) : Parcelable