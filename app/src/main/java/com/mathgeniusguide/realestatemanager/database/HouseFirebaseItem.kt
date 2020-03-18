package com.mathgeniusguide.realestatemanager.database

class HouseFirebaseItem {
    companion object Factory {
        fun create(): HouseFirebaseItem = HouseFirebaseItem()
    }
    var id: String? = null
    var agent: String? = null
    var area: Int? = null
    var bathrooms: Int? = null
    var bedrooms: Int? = null
    var borough: String? = null
    var description: String? = null
    var images: String? = null
    var latitude: Double? = null
    var listDate: String? = null
    var location: String? = null
    var longitude: Double? = null
    var price: Int? = null
    var rooms: Int? = null
    var saleDate: String? = null
    var type: Int? = null
}