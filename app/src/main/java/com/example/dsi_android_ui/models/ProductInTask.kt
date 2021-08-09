package com.example.dsi_android_ui.models

import java.io.Serializable

data class ProductInTask(
    var sku: String = "",
    var productName: String = "",
    var status: String = "",
    var count: Int = 0,
    var gtin: String = "",
    var locations: List<LocationInTask> = listOf(),
) : Serializable {

    fun getTotal(): Int = (locations.map { item -> item.count }
        .reduce { acc, i ->
            acc.plus(i)
        })
}
