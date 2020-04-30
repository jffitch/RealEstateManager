package com.mathgeniusguide.realestatemanager.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.mathgeniusguide.realestatemanager.objects.HouseLocation
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import com.mathgeniusguide.realestatemanager.utils.Functions.fullLocation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowNetworkInfo


class FunctionsTest {
    private var connectivityManager: ConnectivityManager? = null
    private var shadowOfActiveNetworkInfo: ShadowNetworkInfo? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        shadowOfActiveNetworkInfo = shadowOf(connectivityManager?.activeNetworkInfo)
    }

    @Test
    fun stringToMediaImage1() {
        val string = "Room 1|Url 1"
        val mediaImage = MediaImage()
        mediaImage.room = "Room 1"
        mediaImage.url = "Url 1"
        val stringMediaImage = string.toMediaImage()
        assertEquals(mediaImage.room, stringMediaImage.room)
        assertEquals(mediaImage.url, stringMediaImage.url)
    }
    @Test
    fun stringToMediaImage2() {
        val string = "Room 2|Url 2|Extra 2"
        val mediaImage = MediaImage()
        mediaImage.room = "Room 2"
        mediaImage.url = "Url 2"
        val stringMediaImage = string.toMediaImage()
        assertEquals(mediaImage.room, stringMediaImage.room)
        assertEquals(mediaImage.url, stringMediaImage.url)
    }
    @Test
    fun stringToMediaImage3() {
        val string = "Room 3"
        val mediaImage = MediaImage()
        mediaImage.room = "Room 3"
        mediaImage.url = ""
        val stringMediaImage = string.toMediaImage()
        assertEquals(mediaImage.room, stringMediaImage.room)
        assertEquals(mediaImage.url, stringMediaImage.url)
    }

    @Test
    fun dollarsToEuros() {
        val dollars = 100
        val euros = 81
        assertEquals(dollars.convertDollarsToEuros(), euros)
    }
    @Test
    fun eurosToDollars() {
        val dollars = 100
        val euros = 81
        assertEquals(dollars, euros.convertEurosToDollars())
    }

    @Test
    fun mediaImageListToTextList1() {
        val image1 = MediaImage()
        image1.room = "Living Room"
        image1.url = "https://example1.com"
        val image2 = MediaImage()
        image2.room = "Bathroom"
        image2.url = "https://example2.com"
        val image3 = MediaImage()
        image3.room = "Bedroom"
        image3.url = "https://example3.com"
        val list = arrayOf(image1, image2, image3).toMutableList()
        val expected = "Living Room: https://example1.com\nBathroom: https://example2.com\nBedroom: https://example3.com"
        val actual = list.toTextList()
        assertEquals(expected, actual)
    }

    @Test
    fun mediaImageListToFirebaseList1() {
        val image1 = MediaImage()
        image1.room = "Living Room"
        image1.url = "https://example1.com"
        val image2 = MediaImage()
        image2.room = "Bathroom"
        image2.url = "https://example2.com"
        val image3 = MediaImage()
        image3.room = "Bedroom"
        image3.url = "https://example3.com"
        val list = arrayOf(image1, image2, image3).toMutableList()
        val expected = "Living Room|https://example1.com||Bathroom|https://example2.com||Bedroom|https://example3.com"
        val actual = list.toFirebaseList()
        assertEquals(expected, actual)
    }

    @Test
    fun stringToMediaImageList1() {
        val string = "Living Room|https://example1.com||Bathroom|https://example2.com||Bedroom|https://example3.com"
        val image1 = MediaImage()
        image1.room = "Living Room"
        image1.url = "https://example1.com"
        val image2 = MediaImage()
        image2.room = "Bathroom"
        image2.url = "https://example2.com"
        val image3 = MediaImage()
        image3.room = "Bedroom"
        image3.url = "https://example3.com"
        val expected = arrayOf(image1, image2, image3).toMutableList()
        val actual = string.toImageList()
        for (i in 0..2) {
            assertEquals(expected[i].room, actual[i].room)
            assertEquals(expected[i].url, actual[i].url)
        }
    }

    @Test
    fun stringToApi1() {
        val address = "1 E 161 St|The Bronx, NY 10451|United States"
        val width = 250
        val height = 250
        val zoom = 10
        val expected = "https://maps.googleapis.com/maps/api/staticmap?center=1 E 161 St,The Bronx, NY 10451&zoom=10&size=250x250&maptype=roadmap&markers=color:red%7C1 E 161 St,The Bronx, NY 10451&key=AIzaSyDMWYwVXRhuhSQ5vcom9iAI2-FH6T6QKDI"
        val actual = address.toAPI(width, height, zoom)
        assertEquals(expected, actual)
    }

    @Test
    fun stringToApi2() {
        val address = "89 E 42nd St|New York, NY 10017|United States"
        val width = 300
        val height = 300
        val zoom = 12
        val expected = "https://maps.googleapis.com/maps/api/staticmap?center=89 E 42nd St,New York, NY 10017&zoom=12&size=300x300&maptype=roadmap&markers=color:red%7C89 E 42nd St,New York, NY 10017&key=AIzaSyDMWYwVXRhuhSQ5vcom9iAI2-FH6T6QKDI"
        val actual = address.toAPI(width, height, zoom)
        assertEquals(expected, actual)
    }

    @Test
    fun stringSplitLocation1() {
        val address = "1 E 161 St|The Bronx, NY 10451|United States"
        val expected = HouseLocation()
        expected.address = "1 E 161 St"
        expected.city = "The Bronx"
        expected.zip = "10451"
        val actual = address.splitLocation()
        assertEquals(expected.address, actual.address)
        assertEquals(expected.city, actual.city)
        assertEquals(expected.zip, actual.zip)
    }

    @Test
    fun stringSplitLocation2() {
        val address = "89 E 42nd St|New York, NY 10017|United States"
        val expected = HouseLocation()
        expected.address = "89 E 42nd St"
        expected.city = "New York"
        expected.zip = "10017"
        val actual = address.splitLocation()
        assertEquals(expected.address, actual.address)
        assertEquals(expected.city, actual.city)
        assertEquals(expected.zip, actual.zip)
    }

    @Test
    fun fullLocation1() {
        val address = "1 E 161 St"
        val city = "The Bronx"
        val zip = "10451"
        val expected = "1 E 161 St|The Bronx, NY 10451|United States"
        val actual = fullLocation(address, city, zip)
        assertEquals(expected, actual)
    }

    @Test
    fun fullLocation2() {
        val address = "89 E 42nd St"
        val city = "New York"
        val zip = "10017"
        val expected = "89 E 42nd St|New York, NY 10017|United States"
        val actual = fullLocation(address, city, zip)
        assertEquals(expected, actual)
    }
}