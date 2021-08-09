package com.example.dsi_android_ui.task_products

data class TaskProduct(
    var productName: String,
    var productIdentifier: String,
    var status: String,
    var productCount: Int,
    var selected: Int,
    var location: String
)