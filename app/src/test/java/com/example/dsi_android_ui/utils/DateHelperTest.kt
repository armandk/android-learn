package com.example.dsi_android_ui.utils

import java.util.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DateHelperTest {

    private lateinit var target: DateHelper

    @Before
    fun setUp() {
        target = DateHelper()
    }

    /**
     * Unit test for **[DateHelper.convertToString]**
     */
    @Test
    fun `given date when convertToString called then return date string`() {
        //Mock
        val date = Date()
        val expected = target.dateFormat.format(date)
        //actual call
        val actual = target.convertToString(date)
        //verify
        assertEquals(expected, actual)

    }

    /**
     * Unit test for **[DateHelper.convertToDate]**
     */
    @Test
    fun `given date string when convertToDate called then return date`() {
        //Mock
        val expected = Date()
        val dateString = target.dateFormat.format(expected)
        //actual call
        val actual = target.convertToDate(dateString)
        //verify
        assertEquals(expected.toString(), actual.toString())

    }

}