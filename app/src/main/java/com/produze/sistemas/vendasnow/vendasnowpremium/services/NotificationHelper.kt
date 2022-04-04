package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.produze.sistemas.vendasnow.vendasnowpremium.AccountReceivableDetailActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import java.util.*

class NotificationHelper(val context: Context) {



    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("VendasNow", "VendasNow", NotificationManager.IMPORTANCE_DEFAULT ).apply {
                description = "Reminder Channel Description"
            }
            val notificationManager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(title: String, message: String){
        // 1
        createNotificationChannel()
        // 2
        val intent = Intent(context, AccountReceivableDetailActivity:: class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        // 3
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        // 4
//        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.reminder_char)
        // 5
//        val res = this.resources
        val notification = NotificationCompat.Builder(context, "VendasNow")
            .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
//            .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(message)
//            .setStyle(
//                NotificationCompat.BigPictureStyle().bigPicture(icon).bigLargeIcon(null)
//            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        // 6
        NotificationManagerCompat.from(context).notify(UUID.randomUUID().hashCode(), notification)

    }

}