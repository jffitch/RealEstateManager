package com.mathgeniusguide.realestatemanager.responses

data class GeocodeResponse(
        val results: List<GeocodeResult>,
        val status: String
)