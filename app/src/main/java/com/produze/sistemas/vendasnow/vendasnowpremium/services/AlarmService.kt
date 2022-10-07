package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.app.Activity
import android.app.AlarmManager

import android.content.Context.ALARM_SERVICE

import android.app.PendingIntent
import android.app.Service

import android.content.Intent

import android.os.IBinder
import android.os.SystemClock
import androidx.annotation.Nullable


class AlarmService : Service() {
    private val TAG = "AlarmService"
    override fun onCreate() {
        sendBroadcast()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        sendBroadcast()
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        sendBroadcast()
        super.onTaskRemoved(rootIntent)
    }

    private fun sendBroadcast() {
        val alarmManager = getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 10, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            AlarmManager.INTERVAL_HALF_HOUR,
            pendingIntent
        )
    }
}