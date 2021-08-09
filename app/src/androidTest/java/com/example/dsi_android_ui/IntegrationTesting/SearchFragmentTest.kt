package com.example.dsi_android_ui.IntegrationTesting

import android.view.KeyEvent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.dsi_android_ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class SearchFragmentTest {
    @get:Rule val activity = ActivityScenarioRule(MainActivity::class.java)
    //@get:Rule val intentsTestRule = IntentsTestRule(SearchableActivity::class.java)

    @Test
    //Verify the integration between a typed search and the display of the products searched
    fun testTypedSearchList() {
        Espresso.onView(ViewMatchers.withId(R.id.search_src_text))
                .perform(ViewActions.typeText("JAZZ HEARTS PURSE"),ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.product_list))
        Espresso.onView(ViewMatchers.withId(R.id.product_list))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    //Verify the integration between the search by category and getting back to the main screen
    fun testBackSearchButton() {
        Espresso.onView(ViewMatchers.withId(R.id.department_btn))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.department_list))
        Espresso.onView(ViewMatchers.withId(R.id.search_src_text))
                .perform(ViewActions.pressBack())
        Espresso.onView(ViewMatchers.withText("By Product"))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testEraseSearchText() {
        Espresso.onView(ViewMatchers.withId(R.id.search_src_text))
                .perform(ViewActions.typeText("TEA PARTY BIRTHDAY"),ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.product_list))
        Espresso.onView(ViewMatchers.withId(R.id.search_close_btn))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.search_src_text))
                .perform(ViewActions.typeText("COFFEE MUG CAT"),ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
    }

    @Test
    fun testFirstMenuFromSearch() {
        Espresso.onView(ViewMatchers.withId(R.id.search_src_text))
                .perform(ViewActions.typeText("BABY MOUSE RED GINGHAM"),ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.product_list))
        Espresso.onView(ViewMatchers.withText("BABY MOUSE RED GINGHAM DRESS"))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.main_content))
        Espresso.onView(ViewMatchers.withContentDescription("Search"))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("By Product"))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testFirstMenuFromLocation() {
        Espresso.onView(ViewMatchers.withId(R.id.by_location_btn))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
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
        Espresso.onView(ViewMatchers.isRoot())
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
        Espresso.onView(ViewMatchers.isRoot())
                .perform(ViewActions.pressKey(KeyEvent.KEYCODE_BACK))
        Espresso.onView(ViewMatchers.withText("By Location"))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testSwitchingLocationTabs() {
        /*
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.by_location_btn))
        Espresso.onView(ViewMatchers.withId(R.id.by_location_btn))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.browse_by_location_list))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
            .perform(ViewActions.click())

         */
        /////////////////
        Espresso.onView(ViewMatchers.withId(R.id.by_location_btn))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.browse_by_location_list))
        // Not waiting for the needed view
        //Thread.sleep(1_000)
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.view_product_list_button))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.browse_by_location_list))
        Espresso.onView(ViewMatchers.withId(R.id.browse_by_location_list))
                .check(matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.view_location_list_button))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.view_product_list_button))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.browse_by_location_list))
                .check(matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.view_location_list_button))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.view_product_list_button))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.browse_by_location_list))
                .check(matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.view_location_list_button))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.browse_by_location_list), 0))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.browse_by_location_list))
                .check(matches(ViewMatchers.isDisplayed()))
    }

}
