package com.mathgeniusguide.realestatemanager.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Philippe on 21/02/2018.
 */

object Utils {
    var date = Date()

    val todayDate: String
        get() {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return info != null && info.isConnected
    }
}
