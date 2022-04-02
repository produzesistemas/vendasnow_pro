package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtils {

        fun setNotification(calendar: GregorianCalendar, activity: Activity, sale: Sale, account: Account) {

            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)
            var df = SimpleDateFormat("dd/MM/yyyy")
            val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            alarmIntent.putExtra("client", sale.client?.name)
            alarmIntent.putExtra("payment", sale.formPayment?.name)
            alarmIntent.putExtra("dueDate", df.format(account.dueDate))
            alarmIntent.putExtra("value", nFormat.format(account.value))
            alarmIntent.putExtra("timestamp", calendar.timeInMillis)
            alarmIntent.putExtra("idSale", sale.id)
            alarmIntent.putExtra("mNotificationId", account.uniqueIDNotification)
            alarmIntent.putExtra("mRequestCode", account.mRequestCode)
            val pendingIntent = PendingIntent.getBroadcast(activity, account.mRequestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_HOUR,
                pendingIntent
            )

    }


    fun setAlarmManager(calendar: GregorianCalendar, activity: Activity) {
        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(activity, 10, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 1,
            pendingIntent
        )

    }
}