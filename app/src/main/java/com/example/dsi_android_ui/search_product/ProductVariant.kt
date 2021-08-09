package com.example.dsi_android_ui.search_product

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductVariant(
        @SerializedName("productName")
        @Expose
        var productName: String? = null,
        @SerializedName("gtin")
        @Expose
        var gtin: String? = null,
        @SerializedName("count")
        @Expose
        var count: Int = 0
)
