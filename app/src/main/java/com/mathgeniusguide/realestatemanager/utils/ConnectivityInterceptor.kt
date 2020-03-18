package com.mathgeniusguide.realestatemanager.utils

import android.content.Context
import com.mathgeniusguide.realestatemanager.utils.isOnline
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!context.isOnline()) {
            throw NoConnectivityException()
        } else {
            val builder = chain.request().newBuilder()
            return chain.proceed(builder.build())
        }
    }
}