package com.produze.sistemas.vendasnow.vendasnowpremium.services
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, NotificationService::class.java)
//        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))
//        service.putExtra("client", intent.getStringExtra("client"))
//        service.putExtra("payment", intent.getStringExtra("payment"))
//        service.putExtra("dueDate", intent.getStringExtra("dueDate"))
//        service.putExtra("value", intent.getStringExtra("value"))
//        service.putExtra("idSale", intent.getStringExtra("idSale"))
//        service.putExtra("mNotificationId", intent.getIntExtra("mNotificationId", 0))
//        service.putExtra("mRequestCode", intent.getIntExtra("mRequestCode", 0))
        context.startService(service)

//        Toast.makeText(context, "Alarme foi chamado",
//            Toast.LENGTH_LONG).show()
    }

}