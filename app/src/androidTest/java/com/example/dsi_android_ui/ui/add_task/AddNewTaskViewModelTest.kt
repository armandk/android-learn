package com.example.dsi_android_ui.ui.add_task

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dsi_android_ui.*
import com.example.dsi_android_ui.models.ProductInTask
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddNewTaskViewModelTest : TestCase() {
    private lateinit var viewModel: AddNewTaskViewModel

    @get:Rule
    val activityRule = ActivityRule(MainActivity::class.java)

    @Before
    fun setupViewModel() {
        viewModel =
            AddNewTaskViewModelFactory(AddNewTaskRepository()).create(AddNewTaskViewModel::class.java)
    }

    @Test
    fun retrieveEmptyTask() {

    }

    //Previous test that was failing
    /*
    
    @Test
    fun addProductsToTask() {
        viewModel.addProductsToTask(getProducts())
        assertEquals(getProducts().size, viewModel.products.value?.size)
    }
     */

    //Test implementation
    @Test
    fun addProductsToTaskTest() {
        Espresso.onView(ViewMatchers.withId(R.id.tasks))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.listview))
        Espresso.onView(ViewMatchers.withId(R.id.taskFragmentAddTask))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.addNewTaskScrollView))
        Espresso.onView(ViewMatchers.withId(R.id.addNewTaskProducts))
            .perform(ViewActions.scrollTo(),ViewActions.click())
        for(product in getProducts()){
            Espresso.onView(ViewMatchers.withId(getIdOfElementByName("0x0")))
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.search_src_text))
                .perform(ViewActions.typeText(product.productName),ViewActions.closeSoftKeyboard())
            Espresso.onView(ViewMatchers.withId(R.id.search_mag_icon))
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withId(R.id.product_by_location_checkbox))
                .perform(ViewActions.click(),ViewActions.closeSoftKeyboard())
            Espresso.onView(ViewMatchers.withText("ADD"))
                .perform(ViewActions.click())
        }
        for(product in getProducts()){
            Espresso.onView(ViewMatchers.withText(product.productName))
                .check(matches(ViewMatchers.isDisplayed()))
        }
    }


    private fun getProducts(): List<ProductInTask> {
        val products = ArrayList<ProductInTask>()
        val product1 = ProductInTask()
        product1.sku = "8995555217205619173"
        product1.gtin = "8995555217205619173"
        product1.productName = "POTTING SHED TEA MUG - V5"
        product1.status = "Over"
        val product2 = ProductInTask()
        product2.sku = "5191091205348166145"
        product1.gtin = "5191091205348166145"
        product2.productName = "LETTER V BLING KEY RING - V1"
        product2.status = "Over"

        products.add(product1)
        products.add(product2)
        return products
    }

}