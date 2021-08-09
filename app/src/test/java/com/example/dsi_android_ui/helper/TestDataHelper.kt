package com.example.dsi_android_ui.helper

import com.example.dsi_android_ui.models.CartItem
import com.example.dsi_android_ui.models.CartItemList
import com.example.dsi_android_ui.search_department.DepartmentModel
import com.example.dsi_android_ui.search_department.ProductOverview
import com.example.dsi_android_ui.search_department.ProductOverviewList
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.search_product.Product
import com.example.dsi_android_ui.search_product.ProductDetails
import com.example.dsi_android_ui.search_product.ProductVariant
import com.example.dsi_android_ui.search_product.SearchProductModel
import com.example.dsi_android_ui.service.Aisle
import com.example.dsi_android_ui.service.AislesResponseModel
import com.example.dsi_android_ui.service.Bin
import com.example.dsi_android_ui.service.BinsResponseModel
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.service.ProductResponseModel
import com.example.dsi_android_ui.service.Section
import com.example.dsi_android_ui.service.SectionsResponseModel
import com.example.dsi_android_ui.service.Shelf
import com.example.dsi_android_ui.service.ShelvesResponseModel
import com.example.dsi_android_ui.service.model.MoveToLocation
import com.example.dsi_android_ui.utils.FILTERS_ASSIGNEE
import com.example.dsi_android_ui.utils.FILTERS_DUE_DATE
import com.example.dsi_android_ui.utils.FILTERS_MINE
import com.example.dsi_android_ui.utils.FILTERS_PRIORITY
import com.example.dsi_android_ui.utils.FILTERS_STATUS
import com.example.dsi_android_ui.utils.FILTERS_TYPE
import com.example.dsi_android_ui.utils.SORT_CATEGORY
import com.example.dsi_android_ui.utils.STATUS_HIGH
import com.example.dsi_android_ui.utils.STATUS_LOW
import com.example.dsi_android_ui.utils.STATUS_MEDIUM
import java.util.*

/*
* This is a helper class to generate dummy data needed for stubbing/verification using mock data
*/
class TestDataHelper {

    fun generateProductVariants(): List<ProductVariant> {
        return listOf(
                ProductVariant("product1", "gtin1", 12),
                ProductVariant("product2", "gtin2", 13),
                ProductVariant("product3", "gtin3", 14),
                ProductVariant("product4", "gtin4", 15),
                ProductVariant("product5", "gtin5", 12),
        )
    }

    fun generateCartItems(): List<CartItem> {
        return listOf(
            CartItem(
                id = 1,
                user_id = 1,
                sku = "sku",
                aisle = "aisle",
                shelf = "shelf",
                section = "section",
                bin = "bin",
                count = 2,
                department = "department",
                category = "category",
                product_name = "product_name",
                sale_price = 10.23F,
                reg_price = 12.23F,
                status_icon = "status_icon",
                status = "status",
                gtin = "gtin"
            ),
            CartItem(
                id = 2,
                user_id = 1,
                sku = "sku",
                aisle = "aisle",
                shelf = "shelf",
                section = "section",
                bin = "bin",
                count = 2,
                department = "department",
                category = "category",
                product_name = "product_name",
                sale_price = 10.23F,
                reg_price = 12.23F,
                status_icon = "status_icon",
                status = "status",
                gtin = "gtin"
            )
        )
    }

    fun generateCartResponse(): CartItemList {
        return CartItemList(generateCartItems())
    }

    fun generateEmptyCartResponse(): CartItemList {
        return CartItemList(listOf())
    }

    fun generateProductDetails(): ProductDetails {
        val productDetails = ProductDetails()
        val product = Product(
                "sku", "gtin", "dept1", "category", "product1", 10, 3.49F, 5.00F, emptyList(),
                "status icon", "status"
        )
        productDetails.productList = product
        return productDetails
    }

    fun generateSearchProductModel(): SearchProductModel {
        val searchProductModel = SearchProductModel()
        val nameList = listOf("prod1", "prod2")
        searchProductModel.productNames = nameList
        return searchProductModel
    }

    fun generateDepartmentModel(): DepartmentModel {
        val result = HashMap<String, Int>()
        result["dept1"] = 20
        result["dept2"] = 23
        val departmentModel = DepartmentModel()
        departmentModel.result = result
        return departmentModel
    }

    fun generateProductOverviewList(): ProductOverviewList {

        val locations = listOf(Location())
        val data1 = ProductOverview("product1", "sku", "gtin", 20, locations)
        val productList: List<ProductOverview> = listOf(data1)
        val productOverviewList = ProductOverviewList()
        productOverviewList.productList = productList
        return productOverviewList
    }

    fun generateAisles(): AislesResponseModel {
        val data1 = Aisle(200, "A1")
        val data2 = Aisle(400, "A2")
        return AislesResponseModel(listOf(data1, data2))
    }

    fun generateShelves(): ShelvesResponseModel {
        val data1 = Shelf(10, "S1")
        val data2 = Shelf(20, "S2")
        return ShelvesResponseModel(listOf(data1, data2))
    }

    fun generateSections(): SectionsResponseModel {
        val data1 = Section(12, "S1")
        val data2 = Section(21, "S2")
        return SectionsResponseModel(listOf(data1, data2))
    }

    fun generateBins(): BinsResponseModel {
        val data1 = Bin(1, "B1")
        val data2 = Bin(2, "B2")
        return BinsResponseModel(listOf(data1, data2))
    }

    fun generateProducts(): ProductResponseModel {
        val data1 = ProductModel(
                "sku", "gtin", "product1", 25, "dept1", "category1", 24.49F, 25.49F, "status icon",
                "status", "A1", "S1", "S1", "B1", "info", "A1-S1-S1-B1"
        )
        val data2 = ProductModel(
                "sku", "gtin", "product2", 35, "dept2", "category2", 14.49F, 20.49F, "status icon",
                "status", "A1", "S1", "S1", "B1", "info", "A1-S1-S1-B1"
        )
        return ProductResponseModel(listOf(data1, data2))

    }

    fun generateTaskType(): Map<Int, String> {
        val taskTypes: MutableMap<Int, String> = HashMap()
        taskTypes[1] = "Product Movement"
        taskTypes[2] = "Product Restock"
        taskTypes[3] = "Product Out of Stock"
        return taskTypes
    }

    fun generateTaskStatus(): Map<Int, String> {
        val statuses: MutableMap<Int, String> = HashMap()
        statuses[1] = "Open"
        statuses[2] = "In Progress"
        statuses[3] = "Overdue"
        statuses[4] = "Complete"
        return statuses
    }

    fun generateTaskPriority(): Map<Int, String> {
        val priorities: MutableMap<Int, String> = HashMap()
        priorities[1] = "High"
        priorities[2] = "Medium"
        priorities[3] = "Low"
        return priorities
    }

    fun generateTaskDueTimes(): Map<Int, String> {
        val priorities: MutableMap<Int, String> = HashMap()
        priorities[1] = "Start of Day"
        priorities[2] = "Mid-Day"
        priorities[3] = "End of Day"
        return priorities
    }

    fun generateFilters(choice: Int): Map<String, List<String>> {
        val filters: MutableMap<String, List<String>> = HashMap()
        val taskTypeFilter: List<String> = ArrayList(listOf("Product Movement"))
        val taskStatusFilterOpenComplete: List<String> = ArrayList(listOf("Open", "Complete"))
        val taskStatusFilterOverdue: List<String> = ArrayList(listOf("Overdue"))
        val taskPriorityFilterHighMed: List<String> = ArrayList(
                listOf(STATUS_HIGH, STATUS_MEDIUM)
        )
        val taskPriorityFilterLow: List<String> = ArrayList(listOf(STATUS_LOW))
        val taskPriorityFilterHigh: List<String> = ArrayList(listOf(STATUS_HIGH))
        val taskAssigneeFilter: List<String> = ArrayList(listOf(FILTERS_MINE))
        val taskDueDateFilter: List<String> = ArrayList(
                listOf("2021-06-02 00:00:00", "2021-06-04 00:00:00")
        )
        val sortCategoryByType: List<String> = ArrayList(listOf(FILTERS_TYPE))
        val sortCategoryByStatus: List<String> = ArrayList(listOf(FILTERS_STATUS))
        val sortCategoryByPriority: List<String> = ArrayList(listOf(FILTERS_PRIORITY))
        val sortCategoryByDueDate: List<String> = ArrayList(listOf(FILTERS_DUE_DATE))
        when (choice) {
            1 -> {
                filters[FILTERS_TYPE] = taskTypeFilter
                filters[FILTERS_PRIORITY] = ArrayList()
            }
            2 -> filters[FILTERS_STATUS] = taskStatusFilterOpenComplete
            3 -> filters[FILTERS_PRIORITY] = taskPriorityFilterLow
            4 -> filters[FILTERS_ASSIGNEE] = taskAssigneeFilter
            5 -> filters[FILTERS_DUE_DATE] = taskDueDateFilter
            6 -> {
                filters[FILTERS_STATUS] = taskStatusFilterOverdue
                filters[FILTERS_PRIORITY] = taskPriorityFilterHighMed
            }
            7 -> filters[SORT_CATEGORY] = sortCategoryByType
            8 -> filters[SORT_CATEGORY] = sortCategoryByStatus
            9 -> filters[SORT_CATEGORY] = sortCategoryByPriority
            10 -> {
                filters[FILTERS_TYPE] = taskTypeFilter
                filters[FILTERS_ASSIGNEE] = taskAssigneeFilter
                filters[SORT_CATEGORY] = sortCategoryByDueDate
            }
            11 -> {
                filters[FILTERS_STATUS] = taskStatusFilterOpenComplete
                filters[FILTERS_PRIORITY] = taskPriorityFilterHigh
                filters[FILTERS_TYPE] = taskTypeFilter
                filters[FILTERS_ASSIGNEE] = taskAssigneeFilter
                filters[FILTERS_DUE_DATE] = taskDueDateFilter
                filters[SORT_CATEGORY] = sortCategoryByPriority
            }

            else -> {
            }
        }
        return filters
    }

    fun generateMoveToLocation(): MoveToLocation {
        return MoveToLocation(

                "sku",
                "gtin",
                "A1",
                "S1",
                "S2",
                "B1",
                "A1",
                "S1",
                "S2",
                "B1",
                20,
                "status",
                "info",
                1,
                5

        )
    }
}