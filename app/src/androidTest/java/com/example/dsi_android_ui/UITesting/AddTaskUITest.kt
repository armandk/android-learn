package com.example.dsi_android_ui.UITesting

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.dsi_android_ui.MainActivity
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.getNthChildOf
import com.example.dsi_android_ui.waitForViewById
import com.sap.cloud.mobile.fiori.formcell.ChoiceFormCell
import com.sap.cloud.mobile.fiori.formcell.NoteFormCell
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddTaskUITest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testSettingNavigationButton() {
        onView(ViewMatchers.withId(R.id.tasks))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.listview))
        onView(ViewMatchers.withId(R.id.taskFragmentAddTask))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.addNewTaskScrollView))
        onView(ViewMatchers.withId(R.id.addNewTaskToolbar))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testDescriptionText() {
        val description = "This is a test"
        onView(ViewMatchers.withId(R.id.tasks))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.listview))
        onView(ViewMatchers.withId(R.id.taskFragmentAddTask))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.addNewTaskScrollView))
        onView(ViewMatchers.withId(R.id.SimplePropertyFormCellValue))
            .perform(ViewActions.typeText(description),ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withText(description))
            .check(matches(ViewMatchers.isDisplayed()))
        //Test previously failing
        /*
        activityRule.scenario.onActivity {
            it.findViewById<NoteFormCell>(R.id.addNewTaskDescription).value = description
        }

        onView(ViewMatchers.withChild(ViewMatchers.withId(R.id.taskFragmentAddTask)))
            .check { _, _ ->
                ViewMatchers.withText(description)
            }

         */
    }

    @Test
    fun testTaskTypeIsSelected() {
        onView(ViewMatchers.withId(R.id.tasks))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.listview))
        onView(ViewMatchers.withId(R.id.taskFragmentAddTask))
            .perform(ViewActions.click())
        onView(ViewMatchers.isRoot())
            .perform(waitForViewById(R.id.addNewTaskScrollView))
        onView(ViewMatchers.withText("Product Movement"))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Product Movement"))
            .check(matches(ViewMatchers.isChecked()))
        /*
        activityRule.scenario.onActivity {
            it.findViewById<ChoiceFormCell>(R.id.addNewTaskTypeOption).value = 1
        }
        onView(ViewMatchers.withChild(ViewMatchers.withId(R.id.addNewTaskTypeOption)))
            .check { view, _ ->
                ViewMatchers.withText("Cycle Counting")
            }

         */
    }
}