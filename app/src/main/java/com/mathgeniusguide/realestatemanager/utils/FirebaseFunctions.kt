package com.mathgeniusguide.realestatemanager.utils

import com.google.firebase.database.DatabaseReference
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.utils.Functions.sendNotification

object FirebaseFunctions {
    fun createHouse(houseItem: HouseFirebaseItem, database: DatabaseReference) {
        val newItem = database.child(Constants.HOUSES).push()
        houseItem.id = newItem.key
        newItem.setValue(houseItem)
    }

    fun updateHouse(houseItem: HouseFirebaseItem, database: DatabaseReference) {
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
    }

    fun updateHouseArea(itemKey: String, area: Int, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("area").setValue(area)
    }

    fun updateHouseBathrooms(itemKey: String, bathrooms: Int, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("bathrooms").setValue(bathrooms)
    }

    fun updateHouseBedrooms(itemKey: String, bedrooms: Int, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("bedrooms").setValue(bedrooms)
    }

    fun updateHouseBorough(itemKey: String, borough: String, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("borough").setValue(borough)
    }

    fun updateHouseDescription(itemKey: String, description: String, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("description").setValue(description)
    }

    fun updateHouseImages(itemKey: String, images: String, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("images").setValue(images)
    }

    fun updateHouseLocation(itemKey: String, location: String, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("location").setValue(location)
    }

    fun updateHousePrice(itemKey: String, price: Int, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("price").setValue(price)
    }

    fun updateHouseRooms(itemKey: String, rooms: Int, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("rooms").setValue(rooms)
    }

    fun updateHouseType(itemKey: String, type: Int, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("type").setValue(type)
    }

    fun deleteHouse(itemKey: String, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.removeValue()
    }
}