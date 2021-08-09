package com.example.dsi_android_ui.cart.model

import com.example.dsi_android_ui.search_product.Location

data class CartToLocationModel(var id: Int, var cart_items: Any?, var location: Location)

/**
[
{
"aisle": "string",
"bin": "string",
"category": "string",
"count": 0,
"department": "string",
"id": 0,
"product_name": "string",
"reg_price": 0,
"sale_price": 0,
"section": "string",
"shelf": "string",
"sku": "string",
"status": "string",
"status_icon": "string",
"user_id": 0
}
]
 */