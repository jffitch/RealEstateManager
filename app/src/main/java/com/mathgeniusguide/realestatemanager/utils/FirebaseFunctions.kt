package com.mathgeniusguide.realestatemanager.utils

import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem

object FirebaseFunctions {
    fun createHouse(houseItem: HouseFirebaseItem, database: DatabaseReference, act: MainActivity) {
        if (act.applicationContext.isOnline()) {
            // create house on Firebase and add to houseItemList variable
            val newItem = database.child(Constants.HOUSES).push()
            houseItem.id = newItem.key
            newItem.setValue(houseItem)
            act.houseItemList.add(houseItem)
            act.houseItemList.sortBy { it.id }
        } else {
            // if no internet connection, show error message
            Toast.makeText(act, act.resources.getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        }
    }

    fun updateHouse(houseItem: HouseFirebaseItem, database: DatabaseReference, act: MainActivity) {
        if (act.applicationContext.isOnline()) {
            // update house on Firebase and update houseItemList variable
            val itemReference = database.child(Constants.HOUSES).child(houseItem.id ?: "x")
            itemReference.child(Constants.STRING_AGENT).setValue(houseItem.agent)
            itemReference.child(Constants.STRING_AREA).setValue(houseItem.area)
            itemReference.child(Constants.STRING_BATHROOMS).setValue(houseItem.bathrooms)
            itemReference.child(Constants.STRING_BEDROOMS).setValue(houseItem.bedrooms)
            itemReference.child(Constants.STRING_BOROUGH).setValue(houseItem.borough)
            itemReference.child(Constants.STRING_DESCRIPTION).setValue(houseItem.description)
            itemReference.child(Constants.STRING_IMAGES).setValue(houseItem.images)
            itemReference.child(Constants.STRING_LATITUDE).setValue(houseItem.latitude)
            itemReference.child(Constants.STRING_LIST_DATE).setValue(houseItem.listDate)
            itemReference.child(Constants.STRING_LOCATION).setValue(houseItem.location)
            itemReference.child(Constants.STRING_LONGITUDE).setValue(houseItem.longitude)
            itemReference.child(Constants.STRING_PRICE).setValue(houseItem.price)
            itemReference.child(Constants.STRING_ROOMS).setValue(houseItem.rooms)
            itemReference.child(Constants.STRING_SALE_DATE).setValue(houseItem.saleDate)
            itemReference.child(Constants.STRING_TYPE).setValue(houseItem.type)
            act.houseItemList = act.houseItemList.filter { it.id != houseItem.id }.toMutableList()
            act.houseItemList.add(houseItem)
            act.houseItemList.sortBy { it.id }
        } else {
            // if no internet connection, show error message
            Toast.makeText(act, act.resources.getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        }
    }
}