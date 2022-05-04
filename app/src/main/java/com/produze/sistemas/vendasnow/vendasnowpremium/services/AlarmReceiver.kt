package com.produze.sistemas.vendasnow.vendasnowpremium.services
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.*
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {
    lateinit var contextBase: Context
    override fun onReceive(context: Context, intent: Intent) {
        contextBase = context
        createWorkRequest("Funciona", 30)
    }

    private fun createWorkRequest(message: String,timeDelayInSeconds: Long  ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val myWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                "title" to "Reminder",
                "message" to message,
            )
            )
            .build()

        WorkManager.getInstance(contextBase).enqueue(myWorkRequest)
    }

}