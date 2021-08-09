package com.example.dsi_android_ui.service

abstract class LocationResponseModel()
data class Aisle(val Count: Int, val Aisle: String)
data class AislesResponseModel(val result: List<Aisle>) : LocationResponseModel()
data class Shelf(val Count: Int, val Shelf: String)
data class ShelvesResponseModel(val result: List<Shelf>) : LocationResponseModel()
data class Section(val Count: Int, val Section: String)
data class SectionsResponseModel(val result: List<Section>) : LocationResponseModel()
data class Bin(val Count: Int, val Bin: String)
data class BinsResponseModel(val result: List<Bin>) : LocationResponseModel()
data class ProductResponseModel(val result: List<ProductModel>) : LocationResponseModel()