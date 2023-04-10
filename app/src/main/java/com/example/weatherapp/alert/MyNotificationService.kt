package com.example.weatherapp.alert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R

class MyNotificationService:  Service() {


    private lateinit var alarmSound: Uri
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val description = intent?.getStringExtra("description")

        notificationChannel(description!!)
        startForeground(2, makeNotification(description))


        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun makeNotification(description: String): Notification {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return NotificationCompat.Builder(applicationContext, "123")
            .setContentText(description)
            .setContentTitle("Weather Alarm")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description)
            ).setSound(alarmSound)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .build()
    }

    private fun notificationChannel(description:String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val channel = NotificationChannel("123",getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.description = description
            channel.setSound(alarmSound,att)
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}