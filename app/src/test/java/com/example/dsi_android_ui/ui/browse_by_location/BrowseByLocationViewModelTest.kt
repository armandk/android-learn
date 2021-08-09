package com.example.dsi_android_ui.ui.browse_by_location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.dsi_android_ui.helper.TestDataHelper
import com.example.dsi_android_ui.service.LocationModel
import com.example.dsi_android_ui.service.LocationService
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.utils.LocationHierarchyLevel
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BrowseByLocationViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    companion object {
        private const val ANY_AISLE = "A1"
        private const val ANY_SHELF = "S1"
        private const val ANY_SECTION = "S1"
        private const val ANY_BIN = "B1"
    }

    private lateinit var target: BrowseByLocationViewModel
    private lateinit var service: LocationService
    private var testDataHelper = TestDataHelper()

    @Before
    fun setUp() {
        service = mockk(relaxed = true)
        target = spyk(BrowseByLocationViewModel(service))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.updateLocation]**
     */
    @Test
    fun `given null and breadcrumbList when updateLocation then update isLocationListSelected to true`() {
        target.updateLocation(null, listOf(ANY_AISLE, ANY_SHELF, ANY_SECTION))

        assert(target.isLocationListSelected)
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.updateLocation]**
     */
    @Test
    fun `given bin level and breadcrumbList when updateLocation then update isLocationListSelected to false`() {
        target.updateLocation(
                LocationHierarchyLevel.BIN, listOf(ANY_AISLE, ANY_SHELF, ANY_SECTION, ANY_BIN)
        )

        assert(!target.isLocationListSelected)
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.updateLocation]**
     */
    @Test
    fun `given aisle level and breadcrumbList when updateLocation then update isLocationListSelected to true`() {
        target.updateLocation(LocationHierarchyLevel.AISLE, listOf(ANY_AISLE))

        assert(target.isLocationListSelected)
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.getListByHierarchyLevel]**
     */
    @Test
    fun `set currentLevel as null when getListByHierarchyLevel then update locationListLiveData with aisles`() {
        //Mock
        target.updateLocation(null, emptyList())
        val expected = testDataHelper.generateAisles().result.map {
            LocationModel(it.Aisle, it.Count, LocationHierarchyLevel.AISLE)
        }
        every {
            service.getAisles(
                    any()
            )
        } answers {
            val response = this.arg<((response: List<LocationModel>?) -> Unit)>(0)
            response.invoke(expected)
        }
        //actual call
        target.getListByHierarchyLevel()
        //verify
        assertEquals(expected, target.locationListLiveData.value)
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.getListByHierarchyLevel]**
     */
    @Test
    fun `set currentLevel as aisle when getListByHierarchyLevel then update locationListLiveData with shelves`() {
        //Mock
        target.updateLocation(LocationHierarchyLevel.AISLE, emptyList())
        val expected = testDataHelper.generateShelves().result.map {
            LocationModel(it.Shelf, it.Count, LocationHierarchyLevel.SHELF)
        }
        every {
            service.getShelvesByAisle(
                    any(), any()
            )
        } answers {
            val response = this.arg<((response: List<LocationModel>?) -> Unit)>(1)
            response.invoke(expected)
        }
        //actual call
        target.getListByHierarchyLevel()
        //verify
        assertEquals(expected, target.locationListLiveData.value)
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.getListByHierarchyLevel]**
     */
    @Test
    fun `set currentLevel as shelf when getListByHierarchyLevel then update locationListLiveData with sections`() {
        //Mock
        target.updateLocation(LocationHierarchyLevel.SHELF, emptyList())

        val expected = testDataHelper.generateSections().result.map {
            LocationModel(it.Section, it.Count, LocationHierarchyLevel.SECTION)
        }
        every {
            service.getSectionsByAisleAndShelf(
                    any(), any(), any()
            )
        } answers {
            val response = this.arg<((response: List<LocationModel>?) -> Unit)>(2)
            response.invoke(expected)
        }
        //actual call
        target.getListByHierarchyLevel()
        //verify
        assertEquals(expected, target.locationListLiveData.value)
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.getListByHierarchyLevel]**
     */
    @Test
    fun `set currentLevel as section when getListByHierarchyLevel then update locationListLiveData with bins`() {
        //Mock
        target.updateLocation(LocationHierarchyLevel.SECTION, emptyList())

        val expected = testDataHelper.generateBins().result.map {
            LocationModel(it.Bin, it.Count, LocationHierarchyLevel.BIN)
        }
        every {
            service.getBinsByAisleShelfAndSection(
                    any(), any(), any(), any()
            )
        } answers {
            val response = this.arg<((response: List<LocationModel>?) -> Unit)>(3)
            response.invoke(expected)
        }
        //actual call
        target.getListByHierarchyLevel()
        //verify
        assertEquals(expected, target.locationListLiveData.value)
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.getListByHierarchyLevel]**
     */
    @Test
    fun `set currentLevel as bin when getListByHierarchyLevel then update locationListLiveData to empty`() {
        //Mock
        target.updateLocation(LocationHierarchyLevel.BIN, emptyList())
        //actual call
        target.getListByHierarchyLevel()
        //verify
        target.locationListLiveData.value?.let { assert(it.isEmpty()) }
    }

    /**
     * Unit test for **[BrowseByLocationViewModel.getProductListByLocation]**
     */
    @Test
    fun `given nothing when getProductListByLocation then update productListLiveData with products`() {
        //Mock
        val expected = testDataHelper.generateProducts().result
        every {
            service.getProductWithLocationDetailsByLocation(
                    any(), any(), any(), any(), any()
            )
        } answers {
            val response = this.arg<((response: List<ProductModel>) -> Unit)>(4)
            response.invoke(expected)
        }
        //actual call
        target.getProductListByLocation()
        //verify
        assertEquals(expected, target.productListLiveData.value)
    }
}