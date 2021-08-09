package com.example.dsi_android_ui.search_product

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductSpecification(
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("value")
        @Expose
        var value: String? = null
)
