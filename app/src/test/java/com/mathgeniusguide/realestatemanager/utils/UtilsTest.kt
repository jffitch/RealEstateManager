package com.mathgeniusguide.realestatemanager.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class UtilsTest {
    @Test
    fun todayDate1() {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        Utils.date = sdf.parse("2019/12/25")
        assertEquals(Utils.todayDate, "12/25/2019")
    }
}