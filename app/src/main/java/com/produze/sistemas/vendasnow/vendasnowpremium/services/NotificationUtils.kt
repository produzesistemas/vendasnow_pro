package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import java.util.*

/**
 * Created by devdeeds.com on 5/12/17.
 */

class NotificationUtils {

//    companion object {
//        private const val HOUR_TO_SHOW_PUSH = 10
//    }

//    fun setNotification(timeInMilliSeconds: Long, activity: Activity) {
        fun setNotification(calendar: GregorianCalendar, activity: Activity) {

        //------------  alarm settings start  -----------------//

//        val alarmPendingIntent by lazy {
//            val intent = Intent(activity.applicationContext, AlarmReceiver::class.java)
//            PendingIntent.getBroadcast(activity.applicationContext, 0, intent, 0)
//        }

//        if (timeInMilliSeconds > 0) {


            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java) // AlarmReceiver1 = broadcast receiver

            alarmIntent.putExtra("reason", "notification")
            alarmIntent.putExtra("timestamp", calendar.timeInMillis)


//            val calendar = GregorianCalendar.getInstance()
//            calendar.timeInMillis = timeInMilliSeconds


            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

//            val calendar = GregorianCalendar.getInstance().apply {
//                if (get(Calendar.HOUR_OF_DAY) >= HOUR_TO_SHOW_PUSH) {
//                    add(Calendar.DAY_OF_MONTH, 1)
//                }
//
//                set(Calendar.HOUR_OF_DAY, HOUR_TO_SHOW_PUSH)
//                set(Calendar.MINUTE, 0)
//                set(Calendar.SECOND, 0)
//                set(Calendar.MILLISECOND, 0)
//            }
//
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_HOUR,
                pendingIntent
            )

//        }

        //------------ end of alarm settings  -----------------//


    }
}