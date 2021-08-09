package com.example.dsi_android_ui.ui.product_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dsi_android_ui.service.LocationDetailsModel
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.utils.LocationHierarchyLevel

open class SelectableProductsViewModel : ViewModel() {
    var selectableProducts = emptyList<SelectableProduct>()
        private set
    var currentConflictResolutionProduct: SelectableProduct? = null
        private set

    private val _selectedProducts = MutableLiveData<List<SelectableProduct>>()
    val selectedProducts: LiveData<List<SelectableProduct>>
        get() = _selectedProducts

    private val selectedConflictResolutionMutableMap =
        mutableMapOf<String, List<LocationDetailsModel>>()

    /**
     * Only set where the initial data is retrieved,
     * otherwise currently selected products will be lost
     */
    fun setupSelectableProducts(
        productList: List<ProductModel>
    ) {
        // TODO - if request is done here, how do you prevent a request from being made, or reset? attach to viewModel and if request hasn't been made, make it?
        selectableProducts = productList.map { SelectableProduct(it, 1) }

        clearSelectedProducts()
    }

    fun onProductSelected(selectableProduct: SelectableProduct) {
        val mutableSelectedProducts = _selectedProducts.value.orEmpty().toMutableList()

        if (!mutableSelectedProducts.contains(selectableProduct)) {
            mutableSelectedProducts.add(selectableProduct)

            _selectedProducts.postValue(mutableSelectedProducts.toList())
        }
    }

    fun onProductDeselected(selectableProduct: SelectableProduct) {
        val mutableSelectedProducts = _selectedProducts.value.orEmpty().toMutableList()

        mutableSelectedProducts.remove(selectableProduct)

        _selectedProducts.postValue(mutableSelectedProducts.toList())
    }

    fun onProductStockSelected(selectedStock: Int, selectableProduct: SelectableProduct) {
        selectableProduct.selectedCount = selectedStock

        _selectedProducts.postValue(_selectedProducts.value.orEmpty().toList())
    }

    /**
     * Use when selectableProducts differs from previously selected
     */
    fun clearSelectedProducts() {
        _selectedProducts.postValue(emptyList())

        selectedConflictResolutionMutableMap.clear()
    }

    /**
     * NOTE: All methods below specific to SelectProductFragments flow
     */
    fun applyConflictResolution(selectedLocationDetails: List<LocationDetailsModel>) {
        currentConflictResolutionProduct?.let {
            selectedConflictResolutionMutableMap[it.product.gtin] = selectedLocationDetails

            val newSelectedStockCount =
                selectedLocationDetails.fold(0) { acc, locationDetailsModel ->
                    acc + locationDetailsModel.Count
                }

            onProductStockSelected(newSelectedStockCount, it)
            onProductSelected(it)

            // Clear to avoid unnecessary caching
            currentConflictResolutionProduct = null
        }
    }

    private fun selectableProductFromLocationDetails(
        selectedStockCount: Int,
        locationDetail: LocationDetailsModel,
        selectableProduct: SelectableProduct
    ): SelectableProduct {
        val product = selectableProduct.product

        val locationPathList = locationDetail.LocationPath.split("-")
        val aisle = locationPathList[LocationHierarchyLevel.AISLE.ordinal]
        val shelf = locationPathList[LocationHierarchyLevel.SHELF.ordinal]
        val section = locationPathList[LocationHierarchyLevel.SECTION.ordinal]
        val bin = locationPathList[LocationHierarchyLevel.BIN.ordinal]

        return SelectableProduct(
            selectedCount = selectedStockCount,
            product = ProductModel(
                gtin = product.gtin,
                sku = product.gtin,
                product_name = product.product_name,
                aisle = aisle,
                shelf = shelf,
                section = section,
                bin = bin,
                count = product.count,
                department = "",
                category = "",
                sale_price = 0.0F,
                reg_price = 0.0F,
                status = "",
                status_icon = "",
                info = "",
                locationPath = "",
            )
        )
    }

    fun getSelectedProductsWithConflictResolutions(): List<SelectableProduct> {
        val selectedProductsWithConflictResolutions = mutableListOf<SelectableProduct>()

        _selectedProducts.value?.forEach { selectableProduct ->
            if (selectedConflictResolutionMutableMap.contains(selectableProduct.product.gtin)) {
                selectedConflictResolutionMutableMap[selectableProduct.product.gtin]?.forEach {
                    selectedProductsWithConflictResolutions.add(
                        selectableProductFromLocationDetails(it.Count, it, selectableProduct)
                    )
                }

            } else {
                selectedProductsWithConflictResolutions.add(
                    selectableProductFromLocationDetails(
                        selectableProduct.selectedCount,
                        selectableProduct.product.Locations[0],
                        selectableProduct
                    )
                )
            }
        }

        return selectedProductsWithConflictResolutions
    }

    fun setProductForConflictResolution(selectableProduct: SelectableProduct) {
        currentConflictResolutionProduct = selectableProduct
    }

    fun getCurrentSelectedConflictResolution(): List<LocationDetailsModel> {
        return selectedConflictResolutionMutableMap[currentConflictResolutionProduct?.product?.gtin]
            ?: emptyList()
    }
}