package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import com.produze.sistemas.vendasnow.vendasnowpremium.AccountReceivableDetailActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import java.util.*
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryAccountReceivable

import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token

class NotificationService : IntentService("NotificationService"){
    private lateinit var mNotification: Notification
    private var mNotificationId: Int = 0
//    private var mRequestCode: Int = 0
    private lateinit var nameClient: String
    private lateinit var payment: String
    private lateinit var dueDate: String
    private lateinit var value: String
    private lateinit var idSale: String
//    private lateinit var auth: FirebaseAuth
    var repository = RepositoryAccountReceivable()
    private var calendar: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token

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
//        auth = FirebaseAuth.getInstance()
//        val user = auth.currentUser
        token = datasource?.get()!!
        if (token.email != null) {

            var timestamp: Long = 0
            if (intent != null && intent.extras != null) {

            }
            val context = this.applicationContext
            var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notifyIntent = Intent(this, AccountReceivableDetailActivity::class.java)
//            val lst = token.email?.let {
//                repository.getAllCurrentDay(calendar, it)
//            } as MutableList<Sale>
//            var dateStart = calendar.time
//            val timeStampStart = Timestamp(dateStart.time)
//            lst.forEach{ sale ->
//                val account = sale.accounts.first {
//                    it.dueDate!! == timeStampStart
//                }
//
//                mNotificationId = UUID.randomUUID().hashCode()
//                notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                val title = "Conta a receber"
//                notifyIntent.putExtra("title", title)
//
//
//                    val message = "Nome do cliente: ${sale.client?.name}" +
//                    " - " + "Forma de pagamento: ${sale.formPayment?.name}" +
//                    " - " + "Data de vencimento: ${account.dueDate}" +
//                    " - " + "Valor a receber: ${account.value}"
//
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
//                notificationManager.notify(mNotificationId, mNotification)
//            }
//
            }



          }

        }

