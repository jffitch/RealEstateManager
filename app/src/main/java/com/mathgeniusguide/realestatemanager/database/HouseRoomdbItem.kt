package com.mathgeniusguide.realestatemanager.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HouseRoomdbItem(
    @PrimaryKey
    val id: String,
    val agent: String?,
    val area: Int?,
    val bathrooms: Int?,
    val bedroooms: Int?,
    val borough: String?,
    val description: String?,
    val images: String?,
    val latitude: Double?,
    val listDate: String?,
    val location: String?,
    val longitude: Double?,
    val price: Int?,
    val rooms: Int?,
    val saleDate: String?,
    val type: Int?
)