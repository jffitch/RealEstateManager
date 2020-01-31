package com.mathgeniusguide.realestatemanager.utils

import android.content.Context
import android.net.wifi.WifiManager

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Philippe on 21/02/2018.
 */

object Utils {

    val todayDate: String
        get() {
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return dateFormat.format(Date())
        }

    fun isInternetAvailable(context: Context): Boolean {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifi.isWifiEnabled
    }
}
