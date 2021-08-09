package com.example.dsi_android_ui.IntegrationTesting

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.dsi_android_ui.MainActivity
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.getNthChildOf
import com.example.dsi_android_ui.waitForViewById
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class TaskFragmentTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testTaskEnabledDisabledTag() {
        Espresso.onView(ViewMatchers.withId(R.id.tasks))
                .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
                .perform(waitForViewById(R.id.listview))
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 1))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.chip_group_single_line), 1))
                .perform(ViewActions.click())
        Espresso.onView(getNthChildOf(ViewMatchers.withId(R.id.listview), 0))
                .check(matches(ViewMatchers.isDisplayed()))
    }

}