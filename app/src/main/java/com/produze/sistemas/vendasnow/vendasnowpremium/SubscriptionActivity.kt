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
import androidx.recyclerview.widget.LinearLayoutManager
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivityAccountReceivableDetailBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivityLoginBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.ActivitySubscriptionBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.services.AlarmReceiver
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterClient
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterPlan
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import kotlinx.coroutines.launch

class SubscriptionActivity : AppCompatActivity() {
    private lateinit var viewModel: SubscriptionViewModel
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var token: Token
    private var datasource: DataSourceUser? = null
    private lateinit var binding: ActivitySubscriptionBinding
    private lateinit var adapterPlan: AdapterPlan


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val toolbar: Toolbar = binding.toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true);
//        supportActionBar?.setDisplayShowHomeEnabled(true);

        try {
            datasource = DataSourceUser(this)
            token = datasource?.get()!!
            if (token.token == "") {

            }

            viewModel = ViewModelProvider(this).get(SubscriptionViewModel::class.java)
            adapterPlan = AdapterPlan(arrayListOf(), viewModel)

            viewModel.loading.observe(this) {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }

            viewModel.complete.observe(this) {
                if (it) {
                    changeActivity()
                }
            }

            viewModel.plans.observe(this) {
                adapterPlan  = AdapterPlan((it), viewModel)
                binding.recyclerView.apply {
                    adapter = adapterPlan
                    layoutManager = LinearLayoutManager(context)
                }
            }

            viewModel.getAllPlan()

        } catch (e: SecurityException) {
            e.message?.let { Log.e("Exception: %s", it) }
        }
    }


    private fun changeActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}