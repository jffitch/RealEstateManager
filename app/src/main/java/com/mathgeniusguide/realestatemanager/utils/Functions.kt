package com.mathgeniusguide.realestatemanager.utils

import android.widget.EditText
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.objects.HouseLocation

object Functions {
    fun fullLocation(address: String, city: String, zip: String): String {
        return "${address}|${city}, NY ${zip}|United States"
    }

    fun filled(vararg fields: EditText): Boolean {
        return fields.all { it.text.isNotEmpty() }
    }
}