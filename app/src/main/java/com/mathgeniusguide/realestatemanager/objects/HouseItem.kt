package com.mathgeniusguide.realestatemanager.objects

class HouseItem {
    companion object Factory {
        fun create(): HouseItem = HouseItem()
    }
    var id: String? = null
    var agent: String? = null
    var area: Int? = null
    var bathrooms: Int? = null
    var bedrooms: Int? = null
    var borough: String? = null
    var description: String? = null
    var images: String? = null
    var listDate: String? = null
    var location: String? = null
    var price: Int? = null
    var rooms: Int? = null
    var saleDate: String? = null
    var type: Int? = null
}