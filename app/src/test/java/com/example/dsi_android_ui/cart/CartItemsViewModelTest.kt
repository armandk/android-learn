package com.example.dsi_android_ui.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.VolleyError
import com.example.dsi_android_ui.cart.model.CartToLocationModel
import com.example.dsi_android_ui.helper.TestDataHelper
import com.example.dsi_android_ui.models.CartItem
import com.example.dsi_android_ui.models.CartItemList
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.utils.CART_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.atLeast
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CartItemsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var target: CartItemsViewModel? = null
    private val testDataHelper = TestDataHelper()

    @Mock
    var cartLiveDataObserver: Observer<List<CartItem>>? = null

    @Mock
    var postCartListObserver: Observer<CartItem>? = null

    @Mock
    var moveCartToLocationObserver: Observer<Boolean>? = null


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        target = CartItemsViewModel()
        cartLiveDataObserver?.let { target!!.cartLiveData.observeForever(it) }
        postCartListObserver?.let { target!!.postCartList.observeForever(it) }
        moveCartToLocationObserver?.let { target!!.locationMoveStatus.observeForever(it) }
    }

    /**
     * Unit test for **[CartItemsViewModel.getCartRequest]**
     */
    @Test
    fun `given cart id when getCartRequest then return get request`() {
        val cartId = 0
        //actual call
        val actual = target?.getCartRequest(cartId)
        //verify
        actual?.url?.let { assert(it.contains(CART_URL)) }
        assert(actual?.method == Request.Method.GET)
    }

    /**
     * Unit test for **[CartItemsViewModel.getCartRequest]**
     */
    @Test
    fun `given error message when getCartRequest then log error`() {
        //actual call
        target?.getCartErrorHandler(VolleyError("Cart is Empty"))
        //Nothing to verify as only logging is present
    }

    /**
     * Unit test for **[CartItemsViewModel.getCartResponseHandler]**
     */
    @Test
    fun `given response when getCartResponseHandler then update cartLiveDataObserver with cart list`() {
        //Mock
        val response = JSONObject(Gson().toJson(testDataHelper.generateCartResponse()))
        //actual call
        target?.getCartResponseHandler(response)
        //verify
        Mockito.verify(cartLiveDataObserver)?.onChanged(
                testDataHelper.generateCartResponse().cart_items
        )
    }

    /**
     * Unit test for **[CartItemsViewModel.getCartResponseHandler]**
     */
    @Test
    fun `given cart is empty response when getCartResponseHandler then update cartLiveDataObserver with empty cart list`() {
        //Mock
        val response = JSONObject(Gson().toJson(testDataHelper.generateEmptyCartResponse()))
        //actual call
        target?.getCartResponseHandler(response)
        //verify
        Mockito.verify(cartLiveDataObserver, atLeast(1))?.onChanged(
                testDataHelper.generateEmptyCartResponse().cart_items
        )
    }

    /**
     * Unit test for **[CartItemsViewModel.postCartRequest]**
     */
    @Test
    fun `given cart id when postCartRequest called then return post request`() {
        //Mock
        val gson = GsonBuilder().serializeNulls().create()
        val data = gson.toJson(CartItemList(testDataHelper.generateCartItems()))

        //actual call
        val actual = target?.postCartRequest(data)
        //verify
        actual?.url?.let { assert(it.contains(CART_URL)) }
        assert(actual?.method == Request.Method.POST)
    }

    /**
     * Unit test for **[CartItemsViewModel.postCartErrorHandler]**
     */
    @Test
    fun `given error message when postCartErrorHandler then update postCartListObserver with null`() {
        //actual call
        target?.postCartErrorHandler(VolleyError("Cart is Empty"))
        //verify
        Mockito.verify(postCartListObserver)?.onChanged(null)
    }

    /**
     * Unit test for **[CartItemsViewModel.postCartResponseHandler]**
     */
    @Test
    fun `given response when postCartResponseHandler then update postCartListObserver with newly added cart data`() {
        //Mock
        val response = JSONObject(Gson().toJson(testDataHelper.generateCartResponse()))
        //actual call
        target?.postCartResponseHandler(response)
        //verify

        Mockito.verify(postCartListObserver)?.onChanged(generateCartResponse().cart_items.first())
    }

    @Test
    fun `given response when moveToLocationResponseHandler update the success failure to UI`(){
        //call
        target?.moveToLocationResponseHandler(true)
        //verify
        Mockito.verify(moveCartToLocationObserver)?.onChanged(true)
    }

    private fun generateCartResponse(): CartItemList {
        val cartItem1 = CartItem(
            id = 1,
            user_id = 1,
            sku = "sku",
            aisle = "aisle",
            shelf = "shelf",
            section = "section",
            bin = "bin",
            count = 2,
            department = "department",
            category = "category",
            product_name = "product_name",
            sale_price = 10.23F,
            reg_price = 12.23F,
            status_icon = "status_icon",
            status = "status",
            gtin = "gtin"
        )

        val cartItem2 = CartItem(
            id = 2,
            user_id = 1,
            sku = "sku",
            aisle = "aisle",
            shelf = "shelf",
            section = "section",
            bin = "bin",
            count = 2,
            department = "department",
            category = "category",
            product_name = "product_name",
            sale_price = 10.23F,
            reg_price = 12.23F,
            status_icon = "status_icon",
            status = "status",
            gtin = "gtin"
        )
        val list = mutableListOf<CartItem>()
        list.add(cartItem1)
        list.add(cartItem2)
        /* Mockito.verify(postCartListObserver)?.onChanged(
                testDataHelper.generateCartResponse().cart_items.first()

        )*/


        return CartItemList(list)
    }

    private fun getCartToLocationRequest(): CartToLocationModel {
        return CartToLocationModel(102,getCartItems(),getLocation())
    }
    private fun getLocation(): Location{
        val loc: Location = Location()
        loc.aisle = "A1"
        loc.section = "S1"
        loc.shelf = "s1"
        loc.bin = "bin"
        return  loc
    }

    private fun getCartItems(): CartItemList {
        val cartItem1 = CartItem(
            id = 102,
            user_id = 1,
            sku = "sku",
            aisle = "aisle",
            shelf = "shelf",
            section = "section",
            bin = "bin",
            count = 2,
            department = "department",
            category = "category",
            product_name = "product_name",
            sale_price = 10.23F,
            reg_price = 12.23F,
            status_icon = "status_icon",
            status = "status",
            gtin = "gtin"
        )

        val cartItem2 = CartItem(
            id = 102,
            user_id = 1,
            sku = "sku",
            aisle = "aisle",
            shelf = "shelf",
            section = "section",
            bin = "bin",
            count = 2,
            department = "department",
            category = "category",
            product_name = "product_name",
            sale_price = 10.23F,
            reg_price = 12.23F,
            status_icon = "status_icon",
            status = "status",
            gtin = "gtin"
        )
        val list = mutableListOf<CartItem>()
        list.add(cartItem1)
        list.add(cartItem2)
        return CartItemList(list)
    }
}