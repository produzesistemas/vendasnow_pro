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
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.services.AlarmReceiver
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.AccountReceivableViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.SaleViewModel
import kotlinx.coroutines.launch

class AccountReceivableDetailActivity : AppCompatActivity() {
    private lateinit var viewModelAccountReceivable: AccountReceivableViewModel
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var token: Token
    private var accountDetail: Account = Account()
    private var datasource: DataSourceUser? = null

    private lateinit var mProgressBar: ProgressBar
    private lateinit var textViewClient: TextView
    private lateinit var textViewDueDate: TextView
    private lateinit var textViewPayment: TextView
    private lateinit var textViewValue: TextView

    private lateinit var mRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_receivable_detail)
//        binding = ActivityAccountReceivableDetailBinding.inflate(layoutInflater)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        mProgressBar = findViewById(R.id.progressBar)
        textViewClient = findViewById(R.id.textViewClient)
        textViewPayment = findViewById(R.id.textViewPayment)
        textViewDueDate = findViewById(R.id.textViewDueDate)
        textViewValue = findViewById(R.id.textViewValue)
        mRadioGroup = findViewById(R.id.radioGroup)
        try {
            datasource = DataSourceUser(this)
            token = datasource?.get()!!
            if (token.token == "") {
                changeActivity()
                finish()
            }

            viewModelAccountReceivable = ViewModelProvider(this).get(AccountReceivableViewModel::class.java)
            val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

            val extras = intent.extras
            if (extras !== null) {
                val isNotification = extras!!.getBoolean("notification")
                val idAccount = extras!!.getString("idAccount")
                if (isNotification) {
                    idAccount?.let { get(it.toInt()) }
                }
            } else {
                Toast.makeText(this, R.string.validation_no_requestCode,
                    Toast.LENGTH_SHORT).show()
            }


            mRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radioButtonToReceive -> {
                        accountDetail.status = 1
                    }
                    R.id.radioButtonReceive -> {
                        accountDetail.status = 2
                    }
                }
            }

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
                    toMainActivity()
                }
            }

            viewModelAccountReceivable.account.observe(this) {
                accountDetail = it
                textViewClient.text = it.sale?.client?.name
                textViewPayment.text = it.sale?.paymentCondition?.description
                textViewDueDate.text = df.format(it.dueDate)
                textViewValue.text = nFormat.format(it.value)

                when (accountDetail.status) {
                    1 -> {mRadioGroup.check(R.id.radioButtonToReceive)}
                    2 -> {mRadioGroup.check(R.id.radioButtonReceive)}
                }
            }

        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }

    private fun get(id: Int) {
        lifecycleScope.launch {
            viewModelAccountReceivable.getById(id, token.token)
        }
    }

    private fun update(account: Account) {
        lifecycleScope.launch {
            viewModelAccountReceivable.save(account, token.token)
        }
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_confirm -> {
                update(accountDetail)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_cancel -> {
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun changeActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}