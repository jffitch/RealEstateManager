package com.mathgeniusguide.realestatemanager.utils

import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String?
        get() = "No Connectivity Exception"

    override fun getLocalizedMessage(): String? {
        return "No connectivity Exception"
    }
}