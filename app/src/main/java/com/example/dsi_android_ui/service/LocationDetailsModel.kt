package com.example.dsi_android_ui.service

import android.os.Parcelable
import com.example.dsi_android_ui.utils.LocationHierarchyLevel
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationDetailsModel(
    val LocationPath: String,
    val Count: Int,
) : Parcelable