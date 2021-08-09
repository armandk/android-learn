package com.example.dsi_android_ui.cart.model

import com.google.gson.annotations.SerializedName
   
data class LocationToCartModel (

   @SerializedName("aisle") var aisle : String,
   @SerializedName("bin") var bin : String,
   @SerializedName("category") var category : String,
   @SerializedName("count") var count : Int,
   @SerializedName("department") var department : String,
   @SerializedName("product_name") var productName : String,
   @SerializedName("reg_price") var regPrice : Int,
   @SerializedName("sale_price") var salePrice : Int,
   @SerializedName("section") var section : String,
   @SerializedName("shelf") var shelf : String,
   @SerializedName("sku") var sku : String,
   @SerializedName("status") var status : String,
   @SerializedName("status_icon") var statusIcon : String,
   @SerializedName("user_id") var userId : Int

)