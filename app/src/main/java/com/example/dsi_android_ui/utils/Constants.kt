package com.example.dsi_android_ui.utils

import com.example.dsi_android_ui.BuildConfig

enum class LocationHierarchyLevel {
    AISLE, SHELF, SECTION, BIN
}

const val DSI_SERVICE_URL = BuildConfig.SERVER_URL
const val LOCATION_SEARCH_URL = "${DSI_SERVICE_URL}/api/v1/productlocation"
const val CART_URL = "${DSI_SERVICE_URL}/api/v1/carts"
const val TASK_SERVICE_URL = "${DSI_SERVICE_URL}/api/v1/tasks"
const val MOVE_TO_LOCATION_URL = "${DSI_SERVICE_URL}/api/v1/locations"
//ProductDetails Recycler view list preview limit
const val MAX_PREVIEW_LIST_LIMIT = 3
//Task list Quick filters
const val FILTERS_MINE = "Me"
const val STATUS_HIGH = "High"
const val STATUS_MEDIUM = "Medium"
const val STATUS_LOW = "Low"
const val SORT_BY_DUE_DATE = "Due Date"
const val QUICK_FILTERS = "QuickFilters"

//Task List Filters
const val FILTERS_TYPE:String = "Task Type"
const val FILTERS_STATUS = "Status"
const val FILTERS_PRIORITY = "Priority"
const val FILTERS_ASSIGNEE = "Assignee"
const val FILTERS_DUE_DATE = "Due Date"
const val FILTER_DUE_DATE_ERROR_MESSAGE = "End date earlier than start date"
const val SORT_CATEGORY = "Sort"
val SORT_CATEGORY_VALUES = arrayOf(FILTERS_TYPE, FILTERS_STATUS, FILTERS_PRIORITY, FILTERS_DUE_DATE)

