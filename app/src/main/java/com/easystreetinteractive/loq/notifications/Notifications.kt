package com.easystreetinteractive.loq.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

interface Notifications{

    fun buildNotification(context: Context): Notification
}

class RealNotifications: Notifications {

    override fun buildNotification(context: Context): Notification {
        val channelId = "default channel"

        val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle(/*resources.getString( R.string.app_name )*/"test")
                .setContentText("Location Service")
                //.setSmallIcon(R.drawable.icon_transparent)
                .setOngoing(true)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "ForegroundChannel",
                    android.app.NotificationManager.IMPORTANCE_LOW)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        return notification
    }


}