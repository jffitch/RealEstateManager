package com.mathgeniusguide.realestatemanager.utils

import com.mathgeniusguide.realestatemanager.objects.MediaImage
import org.junit.Assert.*
import org.junit.Test

class FunctionsTest {
    @Test
    fun stringToMediaImage1() {
        val string = "Room 1|Image 1"
        val mediaImage = MediaImage()
        mediaImage.room = "Room 1"
        mediaImage.url = "Image 1"
        val stringMediaImage = string.toMediaImage()
        assertEquals(mediaImage.room, stringMediaImage.room)
        assertEquals(mediaImage.url, stringMediaImage.url)
    }
    @Test
    fun stringToMediaImage2() {
        val string = "Room 2|Image 2|Extra 2"
        val mediaImage = MediaImage()
        mediaImage.room = "Room 2"
        mediaImage.url = "Image 2"
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
}