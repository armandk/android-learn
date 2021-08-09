package com.example.dsi_android_ui.task_fragment

data class Products (

        val Sku: String,

        val Gtin: String,
        val Department: String,


        val Category: String,


        val ProductName: String,


        val Total: Long,


        val SalePrice: Double,


        val RegPrice: Double,


        val StatusIcon: StatusIcon,


        val Status: Status
)
enum class Status(val value: String) {
    Full("Full"),
    Low("Low"),
    Over("Over");

    companion object {
        public fun fromValue(value: String): Status = when (value) {
            "Full" -> Full
            "Low"  -> Low
            "Over" -> Over
            else   -> throw IllegalArgumentException()
        }
    }
}

enum class StatusIcon(val value: String) {
    TestPNG("/test.png");

    companion object {
        public fun fromValue(value: String): StatusIcon = when (value) {
            "/test.png" -> TestPNG
            else        -> throw IllegalArgumentException()
        }
    }
}
