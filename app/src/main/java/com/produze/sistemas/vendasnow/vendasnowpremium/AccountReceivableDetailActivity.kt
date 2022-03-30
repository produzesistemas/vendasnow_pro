package com.produze.sistemas.vendasnow.vendasnowpremium

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentDetailAccountReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivableDetail
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelSale
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.services.AlarmReceiver
import com.produze.sistemas.vendasnow.vendasnowpremium.services.NotificationUtils


class AccountReceivableDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModelDetailAccountReceivable: ViewModelDetailAccountReceivable
    private lateinit var viewModel: ViewModelSale
    private lateinit var binding: FragmentDetailAccountReceivableBinding
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private var saleDetail: Sale = Sale()
    private lateinit var dueDate: Date
    private var calendarDueDate: Calendar = Calendar.getInstance()

    private lateinit var mProgressBar: ProgressBar
    private lateinit var mTextViewClient: TextView
    private lateinit var mTextViewPayment: TextView
    private lateinit var mTextViewSaleDate: TextView
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_receivable_detail)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        mProgressBar = findViewById(R.id.progressBar)
        mTextViewClient = findViewById(R.id.textViewClient)
        mTextViewPayment = findViewById(R.id.textViewPayment)
        mTextViewSaleDate = findViewById(R.id.textViewSaleDate)
        mRecyclerView = findViewById(R.id.recyclerView)
        try {
            auth = FirebaseAuth.getInstance()
            val user = auth.getCurrentUser()
            if (user == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            viewModel = ViewModelProvider(this).get(ViewModelSale::class.java)
            viewModelDetailAccountReceivable = ViewModelProvider(this).get(ViewModelDetailAccountReceivable::class.java)

            val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            mProgressBar.visibility = View.GONE

            val extras = intent.extras
            if (extras !== null) {
                val isNotification = extras!!.getBoolean("notification")
                val idSale = extras!!.getString("idSale")
                dueDate = df.parse(extras!!.getString("dueDate"))
                calendarDueDate.time = dueDate
                if (isNotification) {
                    idSale?.let { load(it) }
                }
            } else {
                Toast.makeText(this, R.string.validation_no_requestCode,
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }

    private fun load(id: String) {
        lifecycleScope.launch {
            viewModel.getById(id).collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        mProgressBar.visibility = View.VISIBLE
                    }

                    is State.Success -> {
                        mProgressBar.visibility = View.GONE
                        val docReference = state.data
                        saleDetail = state.data.toObject(Sale::class.java)!!
                        saleDetail.id = docReference.id
                        mTextViewClient.text = saleDetail.client?.name
                        mTextViewPayment.text = saleDetail.formPayment?.name
                        mTextViewSaleDate.text = df.format(saleDetail.salesDate)

                        mRecyclerView.apply {
                            adapter = AdapterAccountReceivableDetail(saleDetail.accounts.filter {
                                val calendar = Calendar.getInstance()
                                calendar.time = it.dueDate
                                calendar.get(Calendar.DAY_OF_MONTH) == calendarDueDate.get(Calendar.DAY_OF_MONTH) &&
                                calendar.get(Calendar.YEAR) == calendarDueDate.get(Calendar.YEAR) &&
                                calendar.get(Calendar.MONTH) + 1 == calendarDueDate.get(Calendar.MONTH) + 1
                            })
                            layoutManager = LinearLayoutManager(context)
                        }
                    }

                    is State.Failed -> {
                        mProgressBar.visibility = View.GONE
                        Toast.makeText(
                            applicationContext, state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }





    }
    private fun update(sale: Sale) {
        lifecycleScope.launch {
            viewModel.update(sale).collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        mProgressBar.visibility = View.VISIBLE
                    }

                    is State.Success -> {
                        cancelNotification(sale)
                        mProgressBar.visibility = View.GONE
                        finish()
                    }

                    is State.Failed -> {
                        mProgressBar.visibility = View.GONE
                        Toast.makeText(
                            applicationContext, state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_confirm -> {
                update(saleDetail)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_cancel -> {
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun cancelNotification(saleToNotification: Sale) {
        when (saleToNotification.formPaymentId) {
            4,7 -> {
                var accountToNotification: Account = saleToNotification.accounts.first()
                if (accountToNotification.status === 2) {

                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(accountToNotification.uniqueIDNotification)

                    val alarmManager = getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                    val myIntent = Intent(applicationContext, AlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        applicationContext, accountToNotification.mRequestCode, myIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager.cancel(pendingIntent)
                }
            }
            8 -> {
                for (i in 1..2) {
                    var accountToNotification: Account = saleToNotification.accounts[i]
                    if (accountToNotification.status === 2) {
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(accountToNotification.uniqueIDNotification)
                        val alarmManager = getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                        val myIntent = Intent(applicationContext, AlarmReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext, accountToNotification.mRequestCode, myIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.cancel(pendingIntent)
                    }

                    }

            }
            9 -> {
                for (i in 1..3) {
                    var accountToNotification: Account = saleToNotification.accounts[i]
                    if (accountToNotification.status === 2) {
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(accountToNotification.uniqueIDNotification)
                        val alarmManager = getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                        val myIntent = Intent(applicationContext, AlarmReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext, accountToNotification.mRequestCode, myIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.cancel(pendingIntent)
                    }
                  }
            }
            10 -> {
                for (i in 1..4) {
                    var accountToNotification: Account = saleToNotification.accounts[i]
                    if (accountToNotification.status === 2) {
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(accountToNotification.uniqueIDNotification)
                        val alarmManager = getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                        val myIntent = Intent(applicationContext, AlarmReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext, accountToNotification.mRequestCode, myIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.cancel(pendingIntent)
                    }
                 }
            }

        }

    }

}