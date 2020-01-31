package com.mathgeniusguide.realestatemanager.utils

import android.content.res.Resources
import com.mathgeniusguide.realestatemanager.R

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