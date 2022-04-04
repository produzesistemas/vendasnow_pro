package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import com.produze.sistemas.vendasnow.vendasnowpremium.AccountReceivableDetailActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.MainActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import java.util.*
import android.app.PendingIntent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelAccountReceivable

import androidx.recyclerview.widget.LinearLayoutManager
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.sql.Timestamp

class NotificationService : IntentService("NotificationService"){
    private lateinit var mNotification: Notification
    private var mNotificationId: Int = 0
//    private var mRequestCode: Int = 0
    private lateinit var nameClient: String
    private lateinit var payment: String
    private lateinit var dueDate: String
    private lateinit var value: String
    private lateinit var idSale: String
    private lateinit var auth: FirebaseAuth
    var repository = RepositoryAccountReceivable()
    private var calendar: GregorianCalendar = Calendar.getInstance() as GregorianCalendar

    @SuppressLint("NewApi")
    private fun createChannel() {
        val context = this.applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
        notificationChannel.enableVibration(true)
        notificationChannel.setShowBadge(true)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.parseColor("#e8334a")
        notificationChannel.description = getString(R.string.app_name)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {

        const val CHANNEL_ID = "samples.notification.devdeeds.com.CHANNEL_ID"
        const val CHANNEL_NAME = "Sample Notification"
    }


    override fun onHandleIntent(intent: Intent?) {
        createChannel()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {

            var timestamp: Long = 0
            if (intent != null && intent.extras != null) {
//            timestamp = intent.extras!!.getLong("timestamp")
//            nameClient = intent.extras!!.getString("client").toString()
//            payment = intent.extras!!.getString("payment").toString()
//            dueDate = intent.extras!!.getString("dueDate").toString()
//            value = intent.extras!!.getString("value").toString()
//            idSale = intent.extras!!.getString("idSale").toString()
//            mNotificationId = intent.extras!!.getInt("mNotificationId")
//            mRequestCode = intent.extras!!.getInt("mRequestCode")
            }
            val context = this.applicationContext
            var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, AccountReceivableDetailActivity::class.java)
            val lst = user.email?.let { repository.getAllCurrentDay(calendar, it) } as MutableList<Sale>
            var dateStart = calendar.time
            val timeStampStart = Timestamp(dateStart.time)
            lst.forEach{ sale ->
                val account = sale.accounts.first {
                    it.dueDate!! == timeStampStart
                }

                mNotificationId = UUID.randomUUID().hashCode()
                notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val title = "Conta a receber"
                notifyIntent.putExtra("title", title)


                    val message = "Nome do cliente: ${sale.client?.name}" +
                    " - " + "Forma de pagamento: ${sale.formPayment?.name}" +
                    " - " + "Data de vencimento: ${account.dueDate}" +
                    " - " + "Valor a receber: ${account.value}"

                val stackBuilder = TaskStackBuilder.create(this)
                stackBuilder.addNextIntentWithParentStack(notifyIntent)
                val id = System.currentTimeMillis().toInt()
                val stackIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_IMMUTABLE)
                val res = this.resources
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mNotification = Notification.Builder(this, CHANNEL_ID)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(stackIntent)
                        .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(false)
                        .setContentTitle(title)
                        .setStyle(
                            Notification.BigTextStyle()
                                .bigText(message)
                        )
                        .setContentText(message).build()
                } else {
                    mNotification = Notification.Builder(this)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(stackIntent)
                        .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(false)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(title)
                        .setStyle(
                            Notification.BigTextStyle()
                                .bigText(message)
                        )
                        .setSound(uri)
                        .setContentText(message).build()
                }
//                notificationManager =
//                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(mNotificationId, mNotification)
            }

            }


//            for (i in 1..2) {
//                val context = this.applicationContext
//                var notificationManager: NotificationManager =
//                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                val notifyIntent = Intent(this, AccountReceivableDetailActivity::class.java)
//
//                val calendar = Calendar.getInstance()
//            val message = "Nome do cliente: $nameClient" +
//                    " - " + "Forma de pagamento: $payment" +
//                    " - " + "Data de vencimento: $dueDate" +
//                    " - " + "Valor a receber: $value"

//                val message = UUID.randomUUID().hashCode().toString()
//                mNotificationId = UUID.randomUUID().hashCode()

//            notifyIntent.putExtra("message", message)
//            notifyIntent.putExtra("notification", true)
//            notifyIntent.putExtra("idSale", idSale)
//            notifyIntent.putExtra("dueDate", dueDate)
//            notifyIntent.putExtra("mNotificationId", mNotificationId)
//            notifyIntent.putExtra("mRequestCode", mRequestCode)
//                notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//                val title = "Conta a receber"
//                notifyIntent.putExtra("title", title)
//            calendar.timeInMillis = timestamp

//                val stackBuilder = TaskStackBuilder.create(this)
//                stackBuilder.addNextIntentWithParentStack(notifyIntent)
//                val id = System.currentTimeMillis().toInt()
//                val stackIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_IMMUTABLE)
//                val res = this.resources
//                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    mNotification = Notification.Builder(this, CHANNEL_ID)
//                        // Set the intent that will fire when the user taps the notification
//                        .setContentIntent(stackIntent)
//                        .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
//                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
//                        .setAutoCancel(false)
//                        .setContentTitle(title)
//                        .setStyle(
//                            Notification.BigTextStyle()
//                                .bigText(message)
//                        )
//                        .setContentText(message).build()
//                } else {
//                    mNotification = Notification.Builder(this)
//                        // Set the intent that will fire when the user taps the notification
//                        .setContentIntent(stackIntent)
//                        .setSmallIcon(R.drawable.ic_baseline_attach_money_24)
//                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
//                        .setAutoCancel(false)
//                        .setPriority(Notification.PRIORITY_MAX)
//                        .setContentTitle(title)
//                        .setStyle(
//                            Notification.BigTextStyle()
//                                .bigText(message)
//                        )
//                        .setSound(uri)
//                        .setContentText(message).build()
//                }
//                notificationManager =
//                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.notify(mNotificationId, mNotification)
//            }
          }

        }

