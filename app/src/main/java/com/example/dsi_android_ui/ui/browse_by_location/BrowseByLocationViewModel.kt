package com.example.dsi_android_ui.ui.browse_by_location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dsi_android_ui.service.LocationModel
import com.example.dsi_android_ui.service.LocationService
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.utils.LocationHierarchyLevel

class BrowseByLocationViewModel() : ViewModel() {
    var aisle = ""
        private set
    var shelf = ""
        private set
    var section = ""
        private set
    var bin = ""
        private set
    private var currentLevel: LocationHierarchyLevel? = null
    val locationListLiveData: MutableLiveData<List<LocationModel>> = MutableLiveData(emptyList())
    val productListLiveData: MutableLiveData<List<ProductModel>> = MutableLiveData(emptyList())
    private var locationService: LocationService = LocationService()
    constructor(service: LocationService) : this() {
        locationService = service
    }
    var isLocationListSelected = true

    fun updateLocation(_level: LocationHierarchyLevel?, breadcrumbList: List<String>) {
        currentLevel = _level

        aisle = breadcrumbList.elementAtOrElse(LocationHierarchyLevel.AISLE.ordinal) { "" }
        shelf = breadcrumbList.elementAtOrElse(LocationHierarchyLevel.SHELF.ordinal) { "" }
        section = breadcrumbList.elementAtOrElse(LocationHierarchyLevel.SECTION.ordinal) { "" }
        bin = breadcrumbList.elementAtOrElse(LocationHierarchyLevel.BIN.ordinal) { "" }

        isLocationListSelected = when (currentLevel) {
            null -> true
            LocationHierarchyLevel.BIN -> false
            else -> isLocationListSelected
        }
    }

    fun getListByHierarchyLevel() {
        when (currentLevel) {
            LocationHierarchyLevel.AISLE -> {
                locationService.getShelvesByAisle(aisle, this::updateLocationList)
            }
            LocationHierarchyLevel.SHELF -> {
                locationService.getSectionsByAisleAndShelf(aisle, shelf, this::updateLocationList)
            }
            LocationHierarchyLevel.SECTION -> {
                locationService.getBinsByAisleShelfAndSection(
                    aisle,
                    shelf,
                    section,
                    this::updateLocationList
                )
            }
            LocationHierarchyLevel.BIN -> {
                updateLocationList(emptyList())
            }
            else ->
                locationService.getAisles(this::updateLocationList)
        }
    }

    fun getProductListByLocation() {
        locationService.getProductWithLocationDetailsByLocation(aisle, shelf, section, bin, this::updateProductList)
    }

    private fun updateLocationList(results: List<LocationModel>) {
        locationListLiveData.postValue(results)
    }

    private fun updateProductList(results: List<ProductModel>) {
        productListLiveData.postValue(results)
    }
}