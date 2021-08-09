package com.example.dsi_android_ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: Int,
    val gtin: String,
    val sku: String,
    val aisle: String,
    val shelf: String,
    val section: String,
    val bin: String,
    val count: Int,
    val user_id: Int? = null,
    val product_name: String? = null,
    val department: String? = null,
    val category: String? = null,
    val sale_price: Float? = 0.0F,
    val reg_price: Float? = 0.0F,
    val status_icon: String? = null,
    val status: String? = null,

    // TODO - currently used for a selectable list, should be removed from this model
    var isSelected: Boolean = false,
    var selectedCount: Int = 1
) : Parcelable
