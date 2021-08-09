package com.example.dsi_android_ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.test.InstrumentationRegistry.*
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


fun getNthChildOf(parentMatcher: Matcher<View?>, childPosition: Int): Matcher<View?>? {
    return object : TypeSafeMatcher<View?>() {
        override fun describeTo(description: Description) {
            description.appendText("with $childPosition child view of type parentMatcher")
        }

        override fun matchesSafely(item: View?): Boolean {
            if (item?.parent !is ViewGroup) {
                return parentMatcher.matches(item?.parent)
            }
            val group = item?.parent as ViewGroup
            return parentMatcher.matches(item?.parent) && group.getChildAt(childPosition) == item
        }
    }
}

fun getIdOfElementByName(id: String): Int {
    @Suppress("DEPRECATION") val targetContext: Context = getTargetContext()
    val packageName: String = targetContext.packageName
    return targetContext.resources.getIdentifier(id, "id", packageName)
}

fun waitForViewById(viewId: Int): ViewAction {
    //Default wait will be for a maximum of 4 seconds
    val millis = 2_000
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "wait for a specific view with id <$viewId> during $millis millis."
        }

        override fun perform(uiController: UiController, view: View?) {
            uiController.loopMainThreadUntilIdle()
            val startTime = System.currentTimeMillis()
            val endTime = startTime + millis
            val viewMatcher: Matcher<View> = ViewMatchers.withId(viewId)
            uiController.loopMainThreadForAtLeast(5_00)
            do {
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child)) {
                        return
                    }
                }
                uiController.loopMainThreadForAtLeast(50)
            } while (System.currentTimeMillis() < endTime)
        }
    }
}