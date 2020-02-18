package com.mathgeniusguide.realestatemanager.utils

import android.content.res.Resources
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.objects.MediaImage

fun Int.convertDollarsToEuros(): Int {
    return Math.round(this * 0.812).toInt()
}

fun Int.convertEurosToDollars(): Int {
    return Math.round(this * 1.232).toInt()
}

fun Int.toHouseType(resources: Resources): String {
    return when (this) {
        Constants.HOUSE -> resources.getString(R.string.house)
        Constants.FLAT -> resources.getString(R.string.flat)
        Constants.DUPLEX -> resources.getString(R.string.duplex)
        Constants.PENTHOUSE -> resources.getString(R.string.penthouse)
        else -> ""
    }
}

fun String.toMediaImage(): MediaImage {
    val mediaImage = MediaImage()
    val list = this.split("|")
    mediaImage.room = if (list.size >= 1) list[0] else ""
    mediaImage.url = if (list.size >= 2) list[1] else ""
    return mediaImage
}

fun MutableList<MediaImage>.toTextList(): String {
    return this.map {"${it.room}: ${it.url}"}.joinToString("\n")
}

fun MutableList<MediaImage>.toFirebaseList(): String {
    return this.map {"${it.room}|${it.url}"}.joinToString("||")
}