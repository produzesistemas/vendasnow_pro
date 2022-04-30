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
//        val service = Intent(context, NotificationService::class.java)
//        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))
//        service.putExtra("client", intent.getStringExtra("client"))
//        service.putExtra("payment", intent.getStringExtra("payment"))
//        service.putExtra("dueDate", intent.getStringExtra("dueDate"))
//        service.putExtra("value", intent.getStringExtra("value"))
//        service.putExtra("idSale", intent.getStringExtra("idSale"))
//        service.putExtra("mNotificationId", intent.getIntExtra("mNotificationId", 0))
//            service.putExtra("mRequestCode", UUID.randomUUID().hashCode())
//            context.startService(service)

//        Toast.makeText(context, "Alarme foi chamado",
//            Toast.LENGTH_LONG).show()
    }

    private fun createWorkRequest(message: String,timeDelayInSeconds: Long  ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()



        val myWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.MINUTES)
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