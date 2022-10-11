package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtils {

//    fun setAlarmManager(activity: Activity) {
//        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
//        val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(activity, 10, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
//
//        alarmManager.setInexactRepeating(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            SystemClock.elapsedRealtime(),
//            AlarmManager.INTERVAL_HALF_HOUR,
//            pendingIntent
//        )
//
//
//
//    }

        fun setAlarmManager(context: Context) {
        val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 10, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            AlarmManager.INTERVAL_HALF_HOUR,
            pendingIntent
        )



    }
}