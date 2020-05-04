package com.mathgeniusguide.realestatemanager.utils

object Constants {
    // API call constants
    const val API_KEY = "AIzaSyDMWYwVXRhuhSQ5vcom9iAI2-FH6T6QKDI"
    const val STATIC_MAPS = "https://maps.googleapis.com/maps/api/staticmap"
    const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    // house types
    const val HOUSE = 0
    const val FLAT = 1
    const val DUPLEX = 2
    const val PENTHOUSE = 3

    // SharedPreferences defaults
    const val PREF = "com.mathgeniusguide.realestatemanager.pref"
    const val LOCATION_ACTUAL = 0
    const val LOCATION_COORDINATES = 1
    const val LOCATION_ADDRESS = 2
    const val LATITUDE_DEFAULT = 40.7537
    const val LONGITUDE_DEFAULT = -73.9992
    const val ADDRESS_DEFAULT = "New York, NY 10001"

    // hardcoded strings
    const val STRING_AGENT = "agent"
    const val STRING_AREA = "area"
    const val STRING_BATHROOMS = "bathrooms"
    const val STRING_BEDROOMS = "bedrooms"
    const val STRING_BOROUGH = "borough"
    const val STRING_DESCRIPTION = "description"
    const val STRING_IMAGES = "images"
    const val STRING_LATITUDE = "latitude"
    const val STRING_LIST_DATE = "listDate"
    const val STRING_LOCATION = "location"
    const val STRING_LONGITUDE = "longitude"
    const val STRING_PRICE = "price"
    const val STRING_ROOMS = "rooms"
    const val STRING_SALE_DATE = "saleDate"
    const val STRING_TYPE = "type"
    const val STRING_LOCATION_METHOD = "locationMethod"
    const val STRING_ADDRESS = "address"
    const val HOUSES = "houses"
}