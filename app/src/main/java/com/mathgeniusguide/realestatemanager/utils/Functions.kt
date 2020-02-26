package com.mathgeniusguide.realestatemanager.utils

object Functions {
    fun fullLocation(address: String, city: String, zip: String): String {
        return "${address}|${city}, NY ${zip}|United States"
    }
}