package com.mathgeniusguide.realestatemanager.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem

object Functions {
    fun fullLocation(address: String, city: String, zip: String): String {
        return "${address}|${city}, NY ${zip}|United States"
    }

    fun filled(vararg fields: EditText): Boolean {
        return fields.all { it.text.isNotEmpty() }
    }

    fun sendNotification(house: HouseFirebaseItem, context: Context, isNew: Boolean) {
        val title = context.resources.getString(if (isNew) R.string.property_added else R.string.property_updated)
        val message = String.format(context.resources.getString(if (isNew) R.string.property_added_details else R.string.property_updated_details),
                house.type?.toHouseType(context.resources),
                "%,d".format(house.price),
                house.borough)
        val notificationManager = NotificationManagerCompat.from(context)
        // create intent, bundle with variables, notification title, and notification message
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("house", house.id)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        // create and send notification
        val notification = NotificationCompat.Builder(context, "notificationChannel")
                .setSmallIcon(R.drawable.image_placeholder)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        notificationManager.notify(1, notification)
    }
}