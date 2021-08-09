package com.example.dsi_android_ui.task_products

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.dsi_android_ui.models.LocationInTask
import com.example.dsi_android_ui.models.ProductInTask

class TaskProductListViewModel : ViewModel() {
    private val mutableList = mutableListOf<TaskProduct>()
    private val _selectedProducts = MutableLiveData<MutableList<TaskProduct>>()
    val selectedProducts: LiveData<MutableList<TaskProduct>> get() = _selectedProducts
    var wasSelectedProductsModified: Boolean = false
    var productsAddedFromTask: Boolean = false

    val productsInTask: LiveData<List<ProductInTask>> = selectedProducts.map { listOfProducts ->
        Log.d("ADD_TASK", "TPLVM#productsInTask$listOfProducts")
        listOfProducts.map { toProductInTask(it) }
    }

    fun addAllToSelected(products: List<TaskProduct>) {
        wasSelectedProductsModified = true
        mutableList.addAll(products)
        _selectedProducts.value = mutableList
        Log.d("ADD_TASK", "TPLVM#addAllToSelected${selectedProducts.value}")
    }


    fun deselect(product: TaskProduct) {
        wasSelectedProductsModified = true
        mutableList.remove(product)
        _selectedProducts.value = mutableList
    }

    private fun toProductInTask(it: TaskProduct): ProductInTask {
        val product = ProductInTask()
        product.productName = it.productName
        product.sku = it.productIdentifier
        product.gtin = it.productIdentifier
        product.status = if (it.status.isEmpty()) "Full" else it.status
        product.locations = listOf(LocationInTask(it.location, "", it.selected))
        return product
    }

    fun addProductsFromTaskToSelectedToList(products: List<ProductInTask>) {
        Log.d("ADD_TASK", "TPLVM#Adding products to selected: ${!productsAddedFromTask}")
        if (!productsAddedFromTask) {
            productsAddedFromTask = true
            val productsFromTask = products.map { toTaskProduct(it) }
            mutableList.clear()
            mutableList.addAll(productsFromTask)
            Log.d("ADD_TASK", "TPLVM#MutableList: $mutableList")
            _selectedProducts.value = mutableList
        }
    }

    private fun toTaskProduct(it: ProductInTask): TaskProduct =
        TaskProduct(
            it.productName,
            it.gtin,
            it.status,
            it.getTotal(),
            it.getTotal(),
            it.locations[0].locationPath
        )


    fun cancelModifications() {
        if (wasSelectedProductsModified) {
            productsAddedFromTask = false
            wasSelectedProductsModified = false
        }
    }

    fun clear() {
        Log.d("ADD_TASK", "TPLVM#Clear")
        productsAddedFromTask = false
        wasSelectedProductsModified = false
        _selectedProducts.value?.clear()
        mutableList.clear()
    }
}