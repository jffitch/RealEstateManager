package com.mathgeniusguide.realestatemanager.utils

import com.google.firebase.database.DatabaseReference
import com.mathgeniusguide.realestatemanager.objects.HouseItem

object Functions {
    fun fullLocation(address: String, city: String, zip: String): String {
        return "${address}|${city}, NY ${zip}|United States"
    }

    fun createHouse(area: Int, bathrooms: Int, bedrooms: Int, borough: String, description: String, images: String, location: String, price: Int, rooms: Int, type: Int, database: DatabaseReference) {
        val newItem = database.child(Constants.HOUSES).push()
        val houseItem = HouseItem.create()
        houseItem.id = newItem.key
        houseItem.area = area
        houseItem.bathrooms = bathrooms
        houseItem.bedrooms = bedrooms
        houseItem.borough = borough
        houseItem.description = description
        houseItem.images = images
        houseItem.location = location
        houseItem.price = price
        houseItem.rooms = rooms
        houseItem.type = type
        newItem.setValue(houseItem)
    }

    fun updateHouse(itemKey: String, area: Int, bathrooms: Int, bedrooms: Int, borough: String, description: String, images: String, location: String, price: Int, rooms: Int, type: Int, database: DatabaseReference) {
        val itemReference = database.child(Constants.HOUSES).child(itemKey)
        itemReference.child("area").setValue(area)
        itemReference.child("bathrooms").setValue(bathrooms)
        itemReference.child("bedrooms").setValue(bedrooms)
        itemReference.child("borough").setValue(borough)
        itemReference.child("description").setValue(description)
        itemReference.child("images").setValue(images)
        itemReference.child("location").setValue(location)
        itemReference.child("price").setValue(price)
        itemReference.child("rooms").setValue(rooms)
        itemReference.child("type").setValue(type)
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