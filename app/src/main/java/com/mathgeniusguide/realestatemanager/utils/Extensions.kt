package com.mathgeniusguide.realestatemanager.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.database.HouseRoomdbItem
import com.mathgeniusguide.realestatemanager.objects.HouseLocation
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

// check internet connection
fun Context.isOnline(): Boolean {
    val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivityManager.activeNetworkInfo
    return info != null && info.isConnected
}

// convert dollars to euros
fun Int.convertDollarsToEuros(): Int {
    return round(this * 0.812).toInt()
}

// convert euros to dollars
fun Int.convertEurosToDollars(): Int {
    return round(this * 1.232).toInt()
}

// convert house type constant to string
fun Int.toHouseType(resources: Resources): String {
    return when (this) {
        Constants.HOUSE -> resources.getString(R.string.house)
        Constants.FLAT -> resources.getString(R.string.flat)
        Constants.DUPLEX -> resources.getString(R.string.duplex)
        Constants.PENTHOUSE -> resources.getString(R.string.penthouse)
        else -> ""
    }
}

// convert checked radio button id to house type constant
fun Int.toHouseTypeConstant(): Int {
    return when (this) {
        R.id.house -> Constants.HOUSE
        R.id.flat -> Constants.FLAT
        R.id.duplex -> Constants.DUPLEX
        R.id.penthouse -> Constants.PENTHOUSE
        else -> -1
    }
}

// convert string to MediaImage object
fun String.toMediaImage(): MediaImage {
    val mediaImage = MediaImage()
    val list = this.split("|")
    mediaImage.room = if (list.isNotEmpty()) list[0] else ""
    mediaImage.url = if (list.size >= 2) list[1] else ""
    return mediaImage
}

// convert MediaImage list to text for AddFragment view
fun MutableList<MediaImage>.toTextList(): String {
    return this.map { "${it.room}: ${it.url}" }.joinToString("\n")
}

// convert MediaImage list to text for Firebase
fun MutableList<MediaImage>.toFirebaseList(): String {
    return this.map { "${it.room}|${it.url}" }.joinToString("||")
}

// convert string to image list for MediaAdapter
fun String.toImageList(): MutableList<MediaImage> {
    return this.split("||").map { it.toMediaImage() }.toMutableList()
}

// convert text address to API call
fun String.toAPI(width: Int, height: Int, zoom: Int): String {
    val address = this.replace("|United States", "").replace("|", ",")
    return "${Constants.STATIC_MAPS}?center=${address}&zoom=${zoom}&size=${width}x${height}&maptype=roadmap&markers=color:red%7C${address}&key=${Constants.API_KEY}"
}

// parse location parts from address string
fun String?.splitLocation(): HouseLocation {
    val houseLocation = HouseLocation()
    if (this == null) {
        return houseLocation
    }
    val locationParts = ("${this}||").split(Regex("\\||, NY"))
    houseLocation.address = locationParts[0].trim()
    houseLocation.city = locationParts[1].trim()
    houseLocation.zip = locationParts[2].trim()
    return houseLocation
}

// convert sorting date to text date
fun String.toDateText(): String {
    try {
        val oldSdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val newSdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        return newSdf.format(oldSdf.parse(this))
    } catch (e: Exception){
        return this
    }
}

// convert Firebase house item to RoomDB house item
fun HouseFirebaseItem.toHouseRoomdbItem(): HouseRoomdbItem {
    return HouseRoomdbItem(
            id = this.id!!,
            agent = this.agent,
            area = this.area,
            bathrooms = this.bathrooms,
            bedroooms = this.bedrooms,
            borough = this.borough,
            description = this.description,
            images = this.images,
            latitude = this.latitude,
            listDate = this.listDate,
            location = this.location,
            longitude = this.longitude,
            price = this.price,
            rooms = this.rooms,
            saleDate = this.saleDate,
            type = this.type
    )
}

// convert RoomDB house item to Firebase house item
fun HouseRoomdbItem.toHouseFirebaseItem(): HouseFirebaseItem {
    val item = HouseFirebaseItem()
    item.id = this.id
    item.agent = this.agent
    item.area = this.area
    item.bathrooms = this.bathrooms
    item.bedrooms = this.bedroooms
    item.borough = this.borough
    item.description = this.description
    item.images = this.images
    item.latitude = this.latitude
    item.listDate = this.listDate
    item.location = this.location
    item.longitude = this.longitude
    item.price = this.price
    item.rooms = this.rooms
    item.saleDate = this.saleDate
    item.type = this.type
    return item
}