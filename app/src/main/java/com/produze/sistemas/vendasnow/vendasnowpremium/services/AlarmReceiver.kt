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

        if(context == null) return
        if(intent == null) return

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
            Toast.makeText(context, "entrou no boot",
                Toast.LENGTH_SHORT).show()
            NotificationUtils().setAlarmManager(context)


        } else {
            Toast.makeText(context, "primeira chamada",
                Toast.LENGTH_SHORT).show()
            contextBase = context
            createWorkRequest("Funciona", 30)
        }



    }

    private fun createWorkRequest(message: String,timeDelayInSeconds: Long  ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        WorkManager.getInstance(contextBase).enqueue(
            OneTimeWorkRequest.from(ReminderWorker::class.java))
    }

}