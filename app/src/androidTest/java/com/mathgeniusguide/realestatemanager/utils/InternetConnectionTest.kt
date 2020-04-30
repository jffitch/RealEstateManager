package com.mathgeniusguide.realestatemanager

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.mathgeniusguide.realestatemanager.utils.Utils
import org.junit.Assert
import org.junit.Test

class InternetConnectionTest {
    @Test
    @Throws(Exception::class)
    fun internetAvailable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Assert.assertTrue(Utils.isInternetAvailable(context))
    }
}