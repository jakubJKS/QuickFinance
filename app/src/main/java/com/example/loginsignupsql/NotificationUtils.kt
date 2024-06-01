package com.example.loginsignupsql

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationUtils {

    private const val CHANNEL_ID = "user_signup_channel"
    private const val CHANNEL_NAME = "User Signup Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for user signups"
    private const val NOTIFICATION_ID = 1

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSignupNotification(context: Context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("NotificationUtils", "Requesting notification permission")
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_ID)
        } else {
            Log.d("NotificationUtils", "Showing notification")
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Signup Successful")
                .setContentText("Your account has been created successfully.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}
