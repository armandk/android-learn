package com.example.dsi_android_ui.service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val sku: String,
    val gtin: String,
    val product_name: String,
    var count: Int,
    val department: String,
    val category: String,
    val sale_price: Float,
    val reg_price: Float,
    val status_icon: String,
    val status: String,
    val aisle: String,
    val shelf: String,
    val section: String,
    val bin: String,
    val info:String,
    val locationPath: String,
    val Locations: List<LocationDetailsModel> = emptyList(),
): Parcelable