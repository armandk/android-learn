package com.example.dsi_android_ui.UITesting

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.dsi_android_ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class TaskUITest {

    @get:Rule val activityRule = ActivityRule(MainActivity::class.java)

    @Test
    fun testTaskListIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.tasks))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.listview))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 1))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.listview))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testTaskFilteringButtons() {
        Espresso.onView(ViewMatchers.withId(R.id.tasks))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.listview))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 4))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 3))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 2))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 1))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 0))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.listview))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testTaskNavigationButton() {
        Espresso.onView(ViewMatchers.withId(R.id.tasks))
                .perform(ViewActions.click())
    }

}