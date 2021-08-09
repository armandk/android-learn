package com.example.dsi_android_ui.service.model

data class MoveToLocation(
        var SKU:String?,
        var gtin:String?,
        var OAisleID:String?,
        var OShelfID:String?,
        var OSectionID:String?,
        var OBinID:String?,
        var NAisleID:String?,
        var NShelfID:String?,
        var NSectionID:String?,
        var NBinID:String?,
        var Count:Int?,
        var Status:String?,
        var Info:String?,
        var Min:Int?,
        var Max:Int?
)
