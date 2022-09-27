package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.produze.sistemas.vendasnow.vendasnowpremium.AccountReceivableDetailActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(val context: Context, val sale: Sale) {

    var df = SimpleDateFormat("dd/MM/yyyy")
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("VendasNow", "VendasNow", NotificationManager.IMPORTANCE_DEFAULT ).apply {
                description = "VendasNow Pro Notification"
            }
            val notificationManager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(){
        createNotificationChannel()
        val account = sale.accounts.firstOrNull()
        val intent = Intent(context, AccountReceivableDetailActivity:: class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.putExtra("notification", true)
        intent.putExtra("idSale", sale.id.toString())
        intent.putExtra("dueDate", df.format(account!!.dueDate))
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(intent)
        val id = System.currentTimeMillis().toInt()
        val stackIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_IMMUTABLE)
        val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val mTitle = "Conta a receber"
        val mMessage = "Nome do cliente: ${sale.client?.name}" +
                " - " + "Forma de pagamento: ${sale.paymentCondition?.description}" +
                " - " + "Data de vencimento: ${df.format(account.dueDate)}" +
                " - " + "Valor a receber: ${nFormat.format(account!!.value)}"
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = NotificationCompat.Builder(context, "VendasNow")
            .setSmallIcon(R.drawable.ic_logo_notification)
            .setContentTitle(mTitle)
            .setContentText(mMessage)
            .setSound(uri)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(mMessage)
            )
            .setContentIntent(stackIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(UUID.randomUUID().hashCode(), notification)

    }

}