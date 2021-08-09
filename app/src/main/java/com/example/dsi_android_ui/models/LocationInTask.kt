package com.example.dsi_android_ui.models

import java.io.Serializable

data class LocationInTask(
    var locationPath: String = "",
    var newLocationPath: String = "",
    var count: Int = 0
): Serializable
