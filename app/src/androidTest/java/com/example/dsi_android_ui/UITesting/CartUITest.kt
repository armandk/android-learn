package com.example.dsi_android_ui.UITesting

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.dsi_android_ui.ActivityRule
import com.example.dsi_android_ui.MainActivity
import com.example.dsi_android_ui.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CartUITest {

    @get:Rule val activityRule = ActivityRule(MainActivity::class.java)

    @Test
    fun testCartNavigationButton() {
        Espresso.onView(ViewMatchers.withId(R.id.work_cart))
                .perform(ViewActions.click())
    }

}