package com.example.dsi_android_ui.viewmodel.location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dsi_android_ui.service.LocationModel
import com.example.dsi_android_ui.service.LocationService
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.service.model.*
import com.example.dsi_android_ui.utils.GenericResponse


class LocationViewModel():ViewModel() {

    private var service:LocationService = LocationService()
    var allAislesLiveData = MutableLiveData<GenericResponse<List<LocationModel>>>()
    var shelfByAisleLiveData = MutableLiveData<GenericResponse<List<LocationModel>>>()
    var sectionByAisleAndShelfLiveData = MutableLiveData<GenericResponse<List<LocationModel>>>()
    var binByAisleAndShelfAndSectionLiveData = MutableLiveData<GenericResponse<List<LocationModel>>>()
    val shelfDetailsByAisleLiveData = MutableLiveData<GenericResponse<List<ProductModel>>>()
    val productDetailsByLocationLiveData = MutableLiveData<GenericResponse<List<ProductModel>>>()
    val productByLocationAndSku = MutableLiveData<ProductModel>()
    val saveLocationLiveData = MutableLiveData<String>()
    constructor(locationService: LocationService) : this() {
        service = locationService
    }
    fun getAisles(){

        service.getAisles {
            allAislesLiveData.value = GenericResponse(
                    GenericResponse.State.SUCCESS,
                    it,
                    null)

        }

    }

    fun getShelfByAisle(aisle:String){
        service.getShelvesByAisle(aisle, onSuccess = {
            shelfByAisleLiveData.value = GenericResponse(
                    GenericResponse.State.SUCCESS,
                    it,
                    null)
        })

    }

    fun getShelfDetailsByAisle(aisle:String){

        service.getProductsByLocation(aisle,null,null,null, onSuccess = {
            shelfDetailsByAisleLiveData.value = GenericResponse(
                    GenericResponse.State.SUCCESS,
                    it,
                    null)
        })
    }

    fun getSectionByAisleAndShelf(aisle:String, shelf: String){

        service.getSectionsByAisleAndShelf(aisle, shelf) {
            sectionByAisleAndShelfLiveData.value = GenericResponse(
                    GenericResponse.State.SUCCESS,
                    it,
                    null)
        }
    }

    fun getBinByAisleAndShelfAndSection(aisle:String, shelf: String, section: String){

        service.getBinsByAisleShelfAndSection(aisle,shelf,section) {
            binByAisleAndShelfAndSectionLiveData.value = GenericResponse(
                    GenericResponse.State.SUCCESS,
                    it,
                    null)
        }
    }

    fun getProductDetailsByLocation(aisle:String, shelf: String, section: String, bin:String){

        service.getProductsByLocation(aisle,shelf,section,bin, onSuccess = {
            productDetailsByLocationLiveData.value = GenericResponse(
                    GenericResponse.State.SUCCESS,
                    it,
                    null)
        })
    }

    fun saveLocation(moveToLocation: MoveToLocation) {
        service.moveToLocation(moveToLocation){
            saveLocationLiveData.value = it
        }
    }

    fun updateLocation(moveToLocation: MoveToLocation) {
        service.updateLocation(moveToLocation){
            saveLocationLiveData.value = it
        }
    }
}