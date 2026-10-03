package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_ID_BOOKINGS = "zucco_bookings_channel"
    const val CHANNEL_NAME_BOOKINGS = "Zucco Bookings & Services"
    const val CHANNEL_DESC_BOOKINGS = "Alerts for artisan booking requests, quotes, and status updates."

    const val NOTIFICATION_ID_WELCOME = 1001
    const val NOTIFICATION_ID_BOOKING_BASE = 2000

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID_BOOKINGS,
                CHANNEL_NAME_BOOKINGS,
                importance
            ).apply {
                description = CHANNEL_DESC_BOOKINGS
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun hasLocationPermission(context: Context): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    @SuppressLint("MissingPermission")
    fun sendNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        subText: String? = null
    ) {
        createNotificationChannels(context)

        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_BOOKINGS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (subText != null) {
            builder.setSubText(subText)
        }

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted or revoked
        }
    }

    fun sendPermissionGrantedWelcome(context: Context) {
        sendNotification(
            context = context,
            notificationId = NOTIFICATION_ID_WELCOME,
            title = "🔔 Notifications Activated",
            message = "You will receive real-time alerts when artisans accept your bookings, send arrival updates, or provide quotes.",
            subText = "Zucco Alerts"
        )
    }

    fun sendBookingSubmittedNotification(
        context: Context,
        providerName: String,
        serviceCategory: String,
        bookingId: Long
    ) {
        sendNotification(
            context = context,
            notificationId = (NOTIFICATION_ID_BOOKING_BASE + (bookingId % 5000)).toInt(),
            title = "📋 Booking Request Sent!",
            message = "Your request for $serviceCategory with $providerName has been submitted. You'll be notified when they accept.",
            subText = "Zucco Bookings"
        )
    }

    fun sendBookingStatusUpdateNotification(
        context: Context,
        providerName: String,
        newStatus: String,
        bookingId: Long
    ) {
        sendNotification(
            context = context,
            notificationId = (NOTIFICATION_ID_BOOKING_BASE + (bookingId % 5000)).toInt(),
            title = "🛠️ Booking Status: $newStatus",
            message = "Artisan $providerName has marked your booking as '$newStatus'. Tap to view details.",
            subText = "Zucco Updates"
        )
    }
}
