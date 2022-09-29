package com.produze.sistemas.vendasnow.vendasnowpremium.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentDetailAccountReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentDetailAccountReceivable : Fragment(){
    private val viewModelDetailAccountReceivable: ViewModelDetailAccountReceivable by activityViewModels()
    private lateinit var viewModelAccountReceivable: AccountReceivableViewModel

    private lateinit var viewModel: SaleViewModel
    private lateinit var binding: FragmentDetailAccountReceivableBinding
    private val viewModelDetailSale: ViewModelDetailSale by activityViewModels()
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private var accountDetail: Account = Account()
    private var year: Int = 0
    private var month: Int = 0
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_detail_account_receivable,
            container,
            false
        )

        return binding.root
    }


    @SuppressLint("ResourceType", "FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {

        }
        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_accounts_receivable))
        viewModel = ViewModelProvider(this).get(SaleViewModel::class.java)
        viewModelAccountReceivable = ViewModelProvider(this).get(AccountReceivableViewModel::class.java)

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.progressBar.visibility = View.GONE
        viewModelDetailAccountReceivable.selectedAccount.observe(viewLifecycleOwner, Observer<Account> { item ->
            accountDetail = item
            load()
        })

        viewModelDetailAccountReceivable.selectedYear.observe(viewLifecycleOwner, Observer<Int> { item ->
            year = item
        })

        viewModelDetailAccountReceivable.selectedMonth.observe(viewLifecycleOwner, Observer<Int> { item ->
            month = item
            load()
        })

        viewModelAccountReceivable.errorMessage.observe(this) {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401) {
                changeActivity()
            }

        }

        viewModelAccountReceivable.loading.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModelAccountReceivable.complete.observe(this, Observer {
            if (it) {
                        view?.findNavController()?.navigate(R.id.nav_account_receivable)
            }
        })

    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_confirm -> {
                update(accountDetail, view)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_cancel -> {
                view?.findNavController()?.navigate(R.id.nav_account_receivable)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun load() {
        binding.textViewClient.text = accountDetail.sale?.client?.name
        binding.textViewPayment.text = accountDetail.sale?.paymentCondition?.description
        binding.textViewDueDate.text = df.format(accountDetail.dueDate)
        binding.textViewValue.text = nFormat.format(accountDetail.value)

        when (accountDetail.status) {
            1 -> {binding.radioGroup.check(R.id.radioButtonToReceive)}
            2 -> {binding.radioGroup.check(R.id.radioButtonReceive)}
        }
    }
    private fun update(account: Account, view: View?) {
        lifecycleScope.launch {
            viewModelAccountReceivable.save(account, token.token)
        }
    }

    private fun changeActivity() {
        activity?.let{
            datasource!!.deleteAll()
            val intent = Intent (it, LoginActivity::class.java)
            it.startActivity(intent)
        }
    }
}