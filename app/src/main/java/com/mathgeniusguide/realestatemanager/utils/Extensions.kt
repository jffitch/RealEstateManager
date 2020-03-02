package com.mathgeniusguide.realestatemanager.utils

import android.content.res.Resources
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.database.HouseRoomdbItem
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

fun Int.toHouseTypeConstant(): Int {
    return when (this) {
        R.id.house -> Constants.HOUSE
        R.id.flat -> Constants.FLAT
        R.id.duplex -> Constants.DUPLEX
        R.id.penthouse -> Constants.PENTHOUSE
        else -> -1
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

fun String.toAPI(width: Int, height: Int, zoom: Int): String {
    //https://maps.googleapis.com/maps/api/staticmap?center=144-85+37th+Avenue,Flushing,NY&zoom=16&size=200x200&maptype=roadmap&key=AIzaSyDMWYwVXRhuhSQ5vcom9iAI2-FH6T6QKDI&markers=color:red%7C144-85+37th+Avenue,Flushing,NY
    //144-85 37th Avenue|Flushing, NY 11354|United States
    //144-85 37th Avenue,Flushing, NY
    val address =  this.replace("|United States", "").replace("|", ",")
    return "${Constants.STATIC_MAPS}?center=${address}&zoom=${zoom}&size=${width}x${height}&maptype=roadmap&markers=color:red%7C${address}&key=${Constants.API_KEY}"
}

fun HouseFirebaseItem.toHouseRoomdbItem(): HouseRoomdbItem {
    val item = HouseRoomdbItem(
            id = this.id!!,
            agent = this.agent,
            area = this.area,
            bathrooms = this.bathrooms,
            bedroooms = this.bedrooms,
            borough = this.borough,
            description = this.description,
            images = this.images,
            listDate = this.listDate,
            location = this.location,
            price = this.price,
            rooms = this.rooms,
            saleDate = this.saleDate,
            type = this.type
    )
    return item
}

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
    item.listDate = this.listDate
    item.location = this.location
    item.price = this.price
    item.rooms = this.rooms
    item.saleDate = this.saleDate
    item.type = this.type
    return item
}