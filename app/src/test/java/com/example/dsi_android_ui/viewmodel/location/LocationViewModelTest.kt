package com.example.dsi_android_ui.viewmodel.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.dsi_android_ui.helper.TestDataHelper
import com.example.dsi_android_ui.service.LocationModel
import com.example.dsi_android_ui.service.LocationService
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.utils.GenericResponse
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
class LocationViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    companion object {
        private const val ANY_AISLE = "A1"
        private const val ANY_SHELF = "S1"
        private const val ANY_SECTION = "S1"
        private const val ANY_BIN = "B1"
        private const val ANY_STRING = "Success"
    }

    private lateinit var target: LocationViewModel
    private lateinit var service: LocationService
    private var testDataHelper = TestDataHelper()

    @Before
    fun setUp() {
        service = mockk(relaxed = true)
        target = spyk(LocationViewModel(service))

    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Unit test for **[LocationViewModel.getAisles]**
     */
    @Test
    fun `given nothing when getAisles called then update allAislesLiveData`() {
        //Mock
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
        target.getAisles()
        //verify
        assertEquals(GenericResponse.State.SUCCESS, target.allAislesLiveData.value?.state)
        assertEquals(expected, target.allAislesLiveData.value?.response)
    }

    /**
     * Unit test for **[LocationViewModel.getShelfByAisle]**
     */
    @Test
    fun `given aisle when getShelfByAisle called then update shelfByAisleLiveData`() {
        //Mock
        val expected = testDataHelper.generateShelves().result.map {
            LocationModel(it.Shelf, it.Count, LocationHierarchyLevel.SHELF)
        }
        every {
            service.getShelvesByAisle(
                    ANY_AISLE, any()
            )
        } answers {
            val response = this.arg<((response: List<LocationModel>?) -> Unit)>(1)
            response.invoke(expected)
        }
        //actual call
        target.getShelfByAisle(ANY_AISLE)
        //verify
        assertEquals(GenericResponse.State.SUCCESS, target.shelfByAisleLiveData.value?.state)
        assertEquals(expected, target.shelfByAisleLiveData.value?.response)
    }

    /**
     * Unit test for **[LocationViewModel.getShelfDetailsByAisle]**
     */
    @Test
    fun `given aisle when getShelfDetailsByAisle called then update shelfDetailsByAisleLiveData`() {
        //Mock
        val expected = testDataHelper.generateProducts().result
        every {
            service.getProductsByLocation(
                    ANY_AISLE, null, null, null, any()
            )
        } answers {
            val response = this.arg<((response: List<ProductModel>) -> Unit)>(4)
            response.invoke(expected)
        }
        //actual call
        target.getShelfDetailsByAisle(ANY_AISLE)
        //verify
        assertEquals(GenericResponse.State.SUCCESS, target.shelfDetailsByAisleLiveData.value?.state)
        assertEquals(expected, target.shelfDetailsByAisleLiveData.value?.response)
    }

    /**
     * Unit test for **[LocationViewModel.getSectionByAisleAndShelf]**
     */
    @Test
    fun `given aisle and shelf when getSectionByAisleAndShelf called then update sectionByAisleAndShelfLiveData`() {
        //Mock
        val expected = testDataHelper.generateSections().result.map {
            LocationModel(it.Section, it.Count, LocationHierarchyLevel.SECTION)
        }
        every {
            service.getSectionsByAisleAndShelf(
                    ANY_AISLE, ANY_SHELF, any()
            )
        } answers {
            val response = this.arg<((response: List<LocationModel>?) -> Unit)>(2)
            response.invoke(expected)
        }
        //actual call
        target.getSectionByAisleAndShelf(ANY_AISLE, ANY_SHELF)
        //verify
        assertEquals(
                GenericResponse.State.SUCCESS, target.sectionByAisleAndShelfLiveData.value?.state
        )
        assertEquals(expected, target.sectionByAisleAndShelfLiveData.value?.response)
    }

    /**
     * Unit test for **[LocationViewModel.getBinByAisleAndShelfAndSection]**
     */
    @Test
    fun `given aisle shelf and section when getBinByAisleAndShelfAndSection called then update sectionByAisleAndShelfLiveData`() {
        //Mock
        val expected = testDataHelper.generateBins().result.map {
            LocationModel(it.Bin, it.Count, LocationHierarchyLevel.BIN)
        }
        every {
            service.getBinsByAisleShelfAndSection(
                    ANY_AISLE, ANY_SHELF, ANY_SECTION, any()
            )
        } answers {
            val response = this.arg<((response: List<LocationModel>?) -> Unit)>(3)
            response.invoke(expected)
        }
        //actual call
        target.getBinByAisleAndShelfAndSection(ANY_AISLE, ANY_SHELF, ANY_SECTION)
        //verify
        assertEquals(
                GenericResponse.State.SUCCESS,
                target.binByAisleAndShelfAndSectionLiveData.value?.state
        )
        assertEquals(expected, target.binByAisleAndShelfAndSectionLiveData.value?.response)
    }

    /**
     * Unit test for **[LocationViewModel.getProductDetailsByLocation]**
     */
    @Test
    fun `given aisle shelf section and bin when getProductDetailsByLocation called then update productDetailsByLocationLiveData`() {
        //Mock
        val expected = testDataHelper.generateProducts().result
        every {
            service.getProductsByLocation(
                    ANY_AISLE, ANY_SHELF, ANY_SECTION, ANY_BIN, any()
            )
        } answers {
            val response = this.arg<((response: List<ProductModel>) -> Unit)>(4)
            response.invoke(expected)
        }
        //actual call
        target.getProductDetailsByLocation(ANY_AISLE, ANY_SHELF, ANY_SECTION, ANY_BIN)
        //verify
        assertEquals(
                GenericResponse.State.SUCCESS, target.productDetailsByLocationLiveData.value?.state
        )
        assertEquals(expected, target.productDetailsByLocationLiveData.value?.response)
    }

    /**
     * Unit test for **[LocationViewModel.saveLocation]**
     */
    @Test
    fun `given MoveToLocation when saveLocation called then update saveLocationLiveData`() {
        //Mock
        val expected = testDataHelper.generateMoveToLocation()
        every {
            service.moveToLocation(
                    expected, any()
            )
        } answers {
            val response = this.arg<((response: String?) -> Unit)>(1)
            response.invoke(ANY_STRING)
        }
        //actual call
        target.saveLocation(expected)
        //verify
        assertEquals(ANY_STRING, target.saveLocationLiveData.value)
    }
}