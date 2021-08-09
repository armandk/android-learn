package com.example.dsi_android_ui.cart

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.example.dsi_android_ui.cart.model.CartToLocationModel
import com.example.dsi_android_ui.cart.model.LocationToCartModel
import com.example.dsi_android_ui.network.VolleyConsumer
import com.example.dsi_android_ui.utils.CART_URL
import com.example.dsi_android_ui.utils.Util
import com.google.gson.JsonObject

class CartService {

    private val TAG = "CartService"

    fun moveToLocation(cartId: Int, cartToLocationModelList: CartToLocationModel, onSuccess: (String?) -> Unit, onFailure: (String?) -> Unit) {

        val url = "$CART_URL/$cartId"
        val data = Util.getJsonObject(cartToLocationModelList)
        val request = JsonObjectRequest(
                Request.Method.PUT,
                url,
                data,
                {
                    onSuccess("success")
                },
                {
                    it.printStackTrace()
                    onFailure(it.message)
                })
        VolleyConsumer.get(request)

    }

    fun moveToCart(cartId: Int, locationToCartModelList: List<LocationToCartModel>, onSuccess: (String?) -> Unit,onFailure: (String?) -> Unit) {

        val url = "$CART_URL/$cartId"
        val request = JsonObjectRequest(
                Request.Method.POST,
                url,
                Util.getJsonObject(locationToCartModelList
                ),
                {
                    onSuccess("success")
                },
                {
                    it.printStackTrace()
                    onFailure(it.message)
                })
        VolleyConsumer.get(request)

    }
}