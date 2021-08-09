package com.example.dsi_android_ui.service

import com.example.dsi_android_ui.helper.TestDataHelper
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationServiceTest {

    private lateinit var target: LocationService
    private var testDataHelper = TestDataHelper()
    private var locationModelList = emptyList<LocationModel>()
    private var productModelList = emptyList<ProductModel>()
    private var onLocationModel: (List<LocationModel>) -> Unit = {
        locationModelList = it
    }
    private var onProductModel: (List<ProductModel>) -> Unit = {
        productModelList = it
    }

    @Before
    fun setUp() {
        target = spyk(LocationService())
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Unit test for **[LocationService.getAisles]**
     */
    @Test
    fun `given callback function when getAisles then update locationModelList with aisles`() {
        //Mock
        every {
            target.requestLocationsByType(
                    any(), AislesResponseModel::class.java, any()
            )
        } answers {
            val response = this.arg<((response: AislesResponseModel?) -> Unit)>(2)
            response.invoke(testDataHelper.generateAisles())
        }
        //actual call
        target.getAisles(onLocationModel)
        //verify
        assert(locationModelList.size == 2)
        assert(locationModelList[0].name == "A1")
        assert(locationModelList[1].name == "A2")
        assert(locationModelList[0].productCount == 200)
        assert(locationModelList[1].productCount == 400)
    }

    /**
     * Unit test for **[LocationService.getShelvesByAisle]**
     */
    @Test
    fun `given aisle and callback function when getShelvesByAisle then update locationModelList with shelves`() {
        //Mock
        every {
            target.requestLocationsByType(
                    any(), ShelvesResponseModel::class.java, any()
            )
        } answers {
            val response = this.arg<((response: ShelvesResponseModel?) -> Unit)>(2)
            response.invoke(testDataHelper.generateShelves())
        }
        //actual call
        target.getShelvesByAisle("A1", onLocationModel)
        //verify
        assert(locationModelList.size == 2)
        assert(locationModelList[0].name == "S1")
        assert(locationModelList[1].name == "S2")
        assert(locationModelList[0].productCount == 10)
        assert(locationModelList[1].productCount == 20)
    }

    /**
     * Unit test for **[LocationService.getSectionsByAisleAndShelf]**
     */
    @Test
    fun `given aisle shelf and callback function when getSectionsByAisleAndShelf then update locationModelList with section`() {
        //Mock
        every {
            target.requestLocationsByType(
                    any(), SectionsResponseModel::class.java, any()
            )
        } answers {
            val response = this.arg<((response: SectionsResponseModel?) -> Unit)>(2)
            response.invoke(testDataHelper.generateSections())
        }
        //actual call
        target.getSectionsByAisleAndShelf("A1", "S1", onLocationModel)
        //verify
        assert(locationModelList.size == 2)
        assert(locationModelList[0].name == "S1")
        assert(locationModelList[1].name == "S2")
        assert(locationModelList[0].productCount == 12)
        assert(locationModelList[1].productCount == 21)
    }

    /**
     * Unit test for **[LocationService.getBinsByAisleShelfAndSection]**
     */
    @Test
    fun `given aisle shelf section and callback function when getBinsByAisleShelfAndSection then update locationModelList with bins`() {
        //Mock
        every {
            target.requestLocationsByType(
                    any(), BinsResponseModel::class.java, any()
            )
        } answers {
            val response = this.arg<((response: BinsResponseModel?) -> Unit)>(2)
            response.invoke(testDataHelper.generateBins())
        }
        //actual call
        target.getBinsByAisleShelfAndSection("A1", "S1", "S1", onLocationModel)
        //verify
        assert(locationModelList.size == 2)
        assert(locationModelList[0].name == "B1")
        assert(locationModelList[1].name == "B2")
        assert(locationModelList[0].productCount == 1)
        assert(locationModelList[1].productCount == 2)
    }

    /**
     * Unit test for **[LocationService.getProductsByLocation]**
     */
    @Test
    fun `given aisle shelf section bin and callback function when getProductsByLocation then update productModelList`() {
        //Mock
        every {
            target.requestLocationsByType(
                    any(), ProductResponseModel::class.java, any()
            )
        } answers {
            val response = this.arg<((response: ProductResponseModel?) -> Unit)>(2)
            response.invoke(testDataHelper.generateProducts())
        }
        val expected = testDataHelper.generateProducts()
        //actual call
        target.getProductsByLocation("A1", "S1", "S1", "B1", onProductModel)
        //verify
        assert(productModelList.size == 2)
        assert(productModelList == expected.result)
    }
}