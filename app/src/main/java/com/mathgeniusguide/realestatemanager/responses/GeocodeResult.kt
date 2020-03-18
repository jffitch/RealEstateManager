package com.mathgeniusguide.realestatemanager.responses

data class GeocodeResult(
        val formatted_address: String,
        val geometry: GeocodeGeometry,
        val place_id: String
)