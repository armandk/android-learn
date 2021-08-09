package com.example.dsi_android_ui.cart

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.dsi_android_ui.cart.model.CartToLocationModel
import com.example.dsi_android_ui.cart.model.LocationToCartModel
import com.example.dsi_android_ui.models.CartItem
import com.example.dsi_android_ui.models.CartItemList
import com.example.dsi_android_ui.network.CustomJsonObjectRequest
import com.example.dsi_android_ui.network.VolleyConsumer
import com.example.dsi_android_ui.ui.product_list.SelectableProduct
import com.example.dsi_android_ui.utils.CART_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject

class CartItemsViewModel : ViewModel() {
    companion object{
        private const val TAG = "CartItemsViewModel"
        private const val URL_PARAM_ID = "id"
    }
    val postCartList: MutableLiveData<CartItem> = MutableLiveData()
    var cartLiveData: MutableLiveData<List<CartItem>> = MutableLiveData(emptyList())
    var locationMoveStatus:MutableLiveData<Boolean> = MutableLiveData()
    var cartMoveStatus:MutableLiveData<Boolean> = MutableLiveData()
    private val cartService = CartService()

    fun getCartItems(cartId: Int) {
        VolleyConsumer.addRequest(getCartRequest(cartId))
    }

    fun getCartRequest(cartId: Int): JsonObjectRequest {
        val requestUrl = Uri.parse(CART_URL)
            .buildUpon()
            .appendQueryParameter(URL_PARAM_ID, cartId.toString())
            .build()
            .toString()

        Log.d(TAG, "URL: $requestUrl")
        return CustomJsonObjectRequest(
            Request.Method.GET, requestUrl, null, this::getCartResponseHandler,
            this::getCartErrorHandler
        )
    }

    internal fun getCartErrorHandler(error: VolleyError?) {
        Log.e(TAG, error.toString())
        cartLiveData.postValue(listOf())
    }

    internal fun getCartResponseHandler(response: JSONObject?) {
        Log.d(TAG, "callSearchByWord: $response")
        var departmentModel: CartItemList? = null
        if (response != null)
            departmentModel = Gson().fromJson(response.toString(), CartItemList::class.java)
        cartLiveData.postValue(departmentModel!!.cart_items)
    }

    fun postCartItems(selectedProducts: List<SelectableProduct>, previousCartId: Int) {
        val cartItems = selectedProducts.map { selectableProduct ->
            val product = selectableProduct.product
            CartItem(
                id = previousCartId,
                gtin = product.gtin,
                sku = product.sku,
                aisle = product.aisle,
                shelf = product.shelf,
                section = product.section,
                bin = product.bin,
                count = selectableProduct.selectedCount,
            )
        }

        val gson = GsonBuilder().serializeNulls().create()
        val data = gson.toJson(CartItemList(cartItems))

        VolleyConsumer.addRequest(postCartRequest(data))
    }

    fun postCartRequest(data: String): JsonObjectRequest {
        val jsonObject = JSONObject(data)
        val requestUrl = Uri.parse(CART_URL)
            .buildUpon()
            .build()
            .toString()

        return JsonObjectRequest(
            Request.Method.POST, requestUrl, jsonObject, this::postCartResponseHandler,
            this::postCartErrorHandler
        )
    }

    internal fun postCartErrorHandler(error: VolleyError?) {
        Log.e(TAG, error.toString())
        postCartList.postValue(null)
    }

    internal fun postCartResponseHandler(response: JSONObject?) {
        Log.d(TAG, "callSearchByWord: $response")
        var cartItems: CartItemList? = null
        if (response != null)
            cartItems = Gson().fromJson(response.toString(), CartItemList::class.java)
        postCartList.postValue(cartItems!!.cart_items.first())
    }

    fun moveToLocation(cartId: Int, cartToLocationModelList: CartToLocationModel){

        cartService.moveToLocation(
                cartId,
                cartToLocationModelList,
                {
                    //success and reloading cartItems
                    moveToLocationResponseHandler(true)
                    getCartItems(cartId)
                }
        ) {
            //show failure message to UI
            moveToLocationResponseHandler(false)
        }
    }

    fun moveToLocationResponseHandler(success : Boolean){
        locationMoveStatus.value = success
    }

    fun moveToCart(cartId: Int, locationToCartModelList: List<LocationToCartModel>){

        cartService.moveToCart(
                cartId,
                locationToCartModelList,
                {
                    //success and reloading location items
                    cartMoveStatus.value = true
                },
                {
                    //show failure message to UI
                    cartMoveStatus.value = false
                }
        )
    }
}