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
            itemReference.child("agent").setValue(houseItem.agent)
            itemReference.child("area").setValue(houseItem.area)
            itemReference.child("bathrooms").setValue(houseItem.bathrooms)
            itemReference.child("bedrooms").setValue(houseItem.bedrooms)
            itemReference.child("borough").setValue(houseItem.borough)
            itemReference.child("description").setValue(houseItem.description)
            itemReference.child("images").setValue(houseItem.images)
            itemReference.child("latitude").setValue(houseItem.latitude)
            itemReference.child("listDate").setValue(houseItem.listDate)
            itemReference.child("location").setValue(houseItem.location)
            itemReference.child("longitude").setValue(houseItem.longitude)
            itemReference.child("price").setValue(houseItem.price)
            itemReference.child("rooms").setValue(houseItem.rooms)
            itemReference.child("saleDate").setValue(houseItem.saleDate)
            itemReference.child("type").setValue(houseItem.type)
            act.houseItemList = act.houseItemList.filter { it.id != houseItem.id }.toMutableList()
            act.houseItemList.add(houseItem)
            act.houseItemList.sortBy { it.id }
        } else {
            // if no internet connection, show error message
            Toast.makeText(act, act.resources.getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        }
    }
}