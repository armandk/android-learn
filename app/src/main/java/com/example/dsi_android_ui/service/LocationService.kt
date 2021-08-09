package com.example.dsi_android_ui.service

import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.dsi_android_ui.network.VolleyConsumer
import com.example.dsi_android_ui.service.model.MoveToLocation
import com.example.dsi_android_ui.utils.LOCATION_SEARCH_URL
import com.example.dsi_android_ui.utils.LocationHierarchyLevel
import com.example.dsi_android_ui.utils.MOVE_TO_LOCATION_URL
import com.google.gson.Gson
import org.json.JSONObject

class LocationService {
    companion object {
        private const val TAG = "LocationService"
        private const val URL_PARAM_AISLES = "aisles"
        private const val URL_PARAM_AISLE = "aisle"
        private const val URL_PARAM_SHELF = "shelf"
        private const val URL_PARAM_SECTION = "section"
        private const val URL_PARAM_BIN = "bin"
        private const val URL_PARAM_DETAIL = "detail"
        private const val URL_PARAM_LOCATION_DETAIL = "loc_detail"
    }

    internal fun <T : LocationResponseModel> requestLocationsByType(
        requestUrl: String,
        responseClass: Class<T>,
        onSuccess: (T?) -> Unit
    ) {
        var parsedResponse: T? = null
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, requestUrl, null,
            { response: JSONObject? ->
                if (response != null) {
                    parsedResponse = Gson().fromJson(response.toString(), responseClass)
                }

                onSuccess(parsedResponse)
            },
            { error ->
                Log.e(TAG, error.toString())
                onSuccess(parsedResponse)
            })
        VolleyConsumer.get(jsonObjectRequest)
    }

    fun getAisles(onSuccess: (List<LocationModel>) -> Unit) {
        val requestUrl = Uri.parse(LOCATION_SEARCH_URL)
            .buildUpon()
            .encodedQuery(URL_PARAM_AISLES)
            .build()
            .toString()

        requestLocationsByType(requestUrl, AislesResponseModel::class.java) { response ->
            var aisles = emptyList<Aisle>()

            response?.result?.let { aisles = it }
            onSuccess(aisles.map {
                LocationModel(it.Aisle, it.Count, LocationHierarchyLevel.AISLE)
            })
        }
    }

    fun getShelvesByAisle(aisle: String, onSuccess: (List<LocationModel>) -> Unit) {
        val requestUrl = Uri.parse(LOCATION_SEARCH_URL)
            .buildUpon()
            .appendQueryParameter(URL_PARAM_AISLE, aisle)
            .build()
            .toString()

        requestLocationsByType(requestUrl, ShelvesResponseModel::class.java) { response ->
            var shelves = emptyList<Shelf>()

            response?.result?.let { shelves = it }
            onSuccess(shelves.map {
                LocationModel(it.Shelf, it.Count, LocationHierarchyLevel.SHELF)
            })
        }
    }

    fun getSectionsByAisleAndShelf(
        aisle: String,
        shelf: String,
        onSuccess: (List<LocationModel>) -> Unit
    ) {
        val requestUrl = Uri.parse(LOCATION_SEARCH_URL)
            .buildUpon()
            .appendQueryParameter(URL_PARAM_AISLE, aisle)
            .appendQueryParameter(URL_PARAM_SHELF, shelf)
            .build()
            .toString()

        requestLocationsByType(requestUrl, SectionsResponseModel::class.java) { response ->
            var sections = emptyList<Section>()

            response?.result?.let { sections = it }
            onSuccess(sections.map {
                LocationModel(it.Section, it.Count, LocationHierarchyLevel.SECTION)
            })
        }
    }

    fun getBinsByAisleShelfAndSection(
        aisle: String,
        shelf: String,
        section: String,
        onSuccess: (List<LocationModel>) -> Unit
    ) {
        val requestUrl = Uri.parse(LOCATION_SEARCH_URL)
            .buildUpon()
            .appendQueryParameter(URL_PARAM_AISLE, aisle)
            .appendQueryParameter(URL_PARAM_SHELF, shelf)
            .appendQueryParameter(URL_PARAM_SECTION, section)
            .build()
            .toString()

        requestLocationsByType(requestUrl, BinsResponseModel::class.java) { response ->
            var bins = emptyList<Bin>()

            response?.result?.let { bins = it }
            onSuccess(bins.map {
                LocationModel(it.Bin, it.Count, LocationHierarchyLevel.BIN)
            })
        }
    }

    fun getProductsByLocation(
        aisle: String,
        shelf: String?,
        section: String?,
        bin: String?,
        onSuccess: (List<ProductModel>) -> Unit
    ) {
        val requestUrl = uriBuilderByLocationDetails(aisle, shelf, section, bin)
            .appendQueryParameter(URL_PARAM_DETAIL, "")
            .build()
            .toString()

        requestLocationsByType(requestUrl, ProductResponseModel::class.java) { response ->
            var products = emptyList<ProductModel>()
            response?.result?.let { products = it }
            onSuccess(products)
        }
    }

    fun getProductWithLocationDetailsByLocation(
        aisle: String,
        shelf: String?,
        section: String?,
        bin: String?,
        onSuccess: (List<ProductModel>) -> Unit
    ) {
        val requestUrl = uriBuilderByLocationDetails(aisle, shelf, section, bin)
            .appendQueryParameter(URL_PARAM_LOCATION_DETAIL, "")
            .build()
            .toString()

        requestLocationsByType(requestUrl, ProductResponseModel::class.java) { response ->
            var products = emptyList<ProductModel>()
            response?.result?.let {
                products = it
            }
            onSuccess(products)
        }
    }

    private fun uriBuilderByLocationDetails(
        aisle: String,
        shelf: String?,
        section: String?,
        bin: String?,
    ): Uri.Builder {
        val uriBuilder = Uri.parse(LOCATION_SEARCH_URL)
            .buildUpon()
            .appendQueryParameter(URL_PARAM_AISLE, aisle)

        if (!shelf.isNullOrEmpty()) uriBuilder.appendQueryParameter(URL_PARAM_SHELF, shelf)
        if (!section.isNullOrEmpty()) uriBuilder.appendQueryParameter(URL_PARAM_SECTION, section)
        if (!bin.isNullOrEmpty()) uriBuilder.appendQueryParameter(URL_PARAM_BIN, bin)

        return uriBuilder
    }

    fun moveToLocation(moveToLocation: MoveToLocation, onSuccess: (String?) -> Unit) {

        val toJson = Gson().toJson(moveToLocation)
        val jsonObject = JSONObject(toJson)
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.POST, MOVE_TO_LOCATION_URL, jsonObject,
                { response: JSONObject? ->
                    var productModel: ProductModel?
                    response.let {
                        productModel = Gson().fromJson(it.toString(), ProductModel::class.java)
                    }
                    if (productModel == null)
                        onSuccess(null)
                    else
                        onSuccess("success")

                },
                { error ->
                    Log.e(TAG, error.toString())
                    onSuccess(null)
                })

        VolleyConsumer.addRequest(jsonObjectRequest)
    }

    fun updateLocation(moveToLocation: MoveToLocation, onSuccess: (String?) -> Unit) {
        val toJson = Gson().toJson(moveToLocation)
        val jsonObject = JSONObject(toJson)
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.PUT, MOVE_TO_LOCATION_URL, jsonObject,
                { response: JSONObject? ->
                    var productModel: ProductModel?
                    response.let {
                        productModel = Gson().fromJson(it.toString(), ProductModel::class.java)
                    }
                    if (productModel == null)
                        onSuccess(null)
                    else
                        onSuccess("success")

                },
                { error ->
                    Log.e(TAG, error.toString())
                    onSuccess(null)
                })

        VolleyConsumer.addRequest(jsonObjectRequest)
    }
}