package com.example.dsi_android_ui.UITesting

import android.view.KeyEvent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.dsi_android_ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchUITest {

    @get:Rule val activityRule = ActivityRule(MainActivity::class.java)

    @Test
    fun testByLocationShelf() {
        Espresso.onView(ViewMatchers.withId(R.id.by_location_btn))
                .perform(ViewActions.click())
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.browse_by_location_list))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.profileHeader))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    ////////////////////////////////////////////////////////////
    @Test
    fun testByLocationProduct() {
        Espresso.onView(ViewMatchers.withId(R.id.by_location_btn))
                .perform(ViewActions.click())
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.browse_by_location_list))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.browse_by_location_list))
        Espresso.onView(ViewMatchers.withId(R.id.view_product_list_button))
                .perform(ViewActions.click())
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.browse_by_location_list))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(isRoot())
            .perform(waitForViewById(R.id.profileHeader))
        Espresso.onView(ViewMatchers.withId(R.id.profileHeader))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testBrowseAllDepartments() {
        Espresso.onView(ViewMatchers.withId(R.id.department_btn))
                .perform(ViewActions.click())
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.department_list))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.department_list), 0))
            .perform(ViewActions.click())
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.department_list))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.department_list), 0))
            .perform(ViewActions.click())
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.department_list))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.department_list), 0))
            .perform(ViewActions.click())
    }

    @Test
    fun testSearchNavigationButton() {
        Espresso.onView(ViewMatchers.withId(R.id.tasks))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.listview))
        Espresso.onView(ViewMatchers.withId(R.id.search))
                .perform(ViewActions.click())
    }

    @Test
    fun testSearchFunctionality() {
        Espresso.onView(ViewMatchers.withId(R.id.search_src_text))
                .perform(ViewActions.typeText("TEA TIME TEA TOW"),ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
        Espresso.onView(isRoot())
                .perform(waitForViewById(R.id.product_list))
        Espresso.onView(ViewMatchers.withText("TEA TIME TEA TOWELS"))
                .perform(ViewActions.click())
    }

}