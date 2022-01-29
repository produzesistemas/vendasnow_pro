package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OnBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, NotificationService::class.java)
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))
        service.putExtra("client", intent.getStringExtra("client"))
        service.putExtra("payment", intent.getStringExtra("payment"))
        service.putExtra("dueDate", intent.getStringExtra("dueDate"))
        service.putExtra("value", intent.getStringExtra("value"))
        context.startService(service)
    }
}