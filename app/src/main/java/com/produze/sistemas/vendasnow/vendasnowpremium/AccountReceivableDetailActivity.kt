package com.produze.sistemas.vendasnow.vendasnowpremium

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

class AccountReceivableDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModelDetailAccountReceivable: ViewModelDetailAccountReceivable
    private lateinit var viewModel: ViewModelSale
    private lateinit var binding: FragmentDetailAccountReceivableBinding
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private var saleDetail: Sale = Sale()

    private lateinit var mProgressBar: ProgressBar
    private lateinit var mTextViewClient: TextView
    private lateinit var mTextViewPayment: TextView
    private lateinit var mTextViewSaleDate: TextView
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_receivable_detail)
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

            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
            viewModel = ViewModelProvider(this).get(ViewModelSale::class.java)
            viewModelDetailAccountReceivable = ViewModelProvider(this).get(ViewModelDetailAccountReceivable::class.java)

            val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            mProgressBar.visibility = View.GONE

            val extras = intent.extras
            if (extras !== null) {
                val isNotification = extras!!.getBoolean("notification")
                val idNotification = extras!!.getInt("mNotificationId")
                val idSale = extras!!.getString("idSale")
                if (isNotification) {
                    idSale?.let { load(it) }
                }
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
                        saleDetail = state.data.toObject(Sale::class.java)!!
                        mTextViewClient.text = saleDetail.client?.name
                        mTextViewPayment.text = saleDetail.formPayment?.name
                        mTextViewSaleDate.text = df.format(saleDetail.salesDate)

                        mRecyclerView.apply {
                            adapter = AdapterAccountReceivableDetail(saleDetail.accounts)
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
                        mProgressBar.visibility = View.GONE

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

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


}