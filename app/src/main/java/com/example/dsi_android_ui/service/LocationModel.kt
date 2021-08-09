package com.example.dsi_android_ui.service

import com.example.dsi_android_ui.utils.LocationHierarchyLevel

class LocationModel(
        var name: String,
        var productCount: Int? = 0,
        var type: LocationHierarchyLevel
){
    override fun toString(): String {
        return  name;
    }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is LocationModel)
        {
            return false
        }
        return this.name.equals(other.name, true)
    }
}
