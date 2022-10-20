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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentDetailAccountReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.widget.RadioGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivityAccountReceivableDetailBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivityLoginBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivitySubscriptionBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.services.AlarmReceiver
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.AccountReceivableViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.SaleViewModel
import kotlinx.coroutines.launch

class SubscriptionActivity : AppCompatActivity() {
    private lateinit var viewModelAccountReceivable: AccountReceivableViewModel
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
//    private lateinit var viewModelMain: ViewModelMain
    private lateinit var token: Token
//    private var accountDetail: Account = Account()
    private var datasource: DataSourceUser? = null
    private lateinit var binding: ActivitySubscriptionBinding
    private lateinit var mProgressBar: ProgressBar
//    private lateinit var textViewClient: TextView
//    private lateinit var textViewDueDate: TextView
//    private lateinit var textViewPayment: TextView
//    private lateinit var textViewValue: TextView

//    private lateinit var mRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_subscription)
//        binding = ActivityAccountReceivableDetailBinding.inflate(layoutInflater)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        mProgressBar = findViewById(R.id.progressBar)

        try {
            datasource = DataSourceUser(this)
            token = datasource?.get()!!
            if (token.token == "") {

            }

            viewModelAccountReceivable = ViewModelProvider(this).get(AccountReceivableViewModel::class.java)

            viewModelAccountReceivable.loading.observe(this) {
                if (it) {
                    mProgressBar.visibility = View.VISIBLE
                } else {
                    mProgressBar.visibility = View.GONE
                }
            }

            viewModelAccountReceivable.complete.observe(this) {
                if (it) {
                    finish()
                    changeActivity()
                }
            }

        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }


    private fun changeActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}