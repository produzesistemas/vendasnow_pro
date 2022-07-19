package com.produze.sistemas.vendasnow.vendasnowpremium.ui.account

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentDetailAccountReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivableDetail
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailSale
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelSale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentDetailAccountReceivable : Fragment(){
    private val viewModelDetailAccountReceivable: ViewModelDetailAccountReceivable by activityViewModels()
    private lateinit var viewModel: ViewModelSale
    private lateinit var binding: FragmentDetailAccountReceivableBinding
    private val viewModelDetailSale: ViewModelDetailSale by activityViewModels()
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private var saleDetail: Sale = Sale()
    private var year: Int = 0
    private var month: Int = 0

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
        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_accounts_receivable))
        viewModel = ViewModelProvider(this).get(ViewModelSale::class.java)
        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.progressBar.visibility = View.GONE
        viewModelDetailAccountReceivable.selectedAccount.observe(viewLifecycleOwner, Observer<Sale> { item ->
            saleDetail = item
        })

        viewModelDetailAccountReceivable.selectedYear.observe(viewLifecycleOwner, Observer<Int> { item ->
            year = item
        })

        viewModelDetailAccountReceivable.selectedMonth.observe(viewLifecycleOwner, Observer<Int> { item ->
            month = item
            load()
        })

        viewModelDetailSale.totalSale.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalSale.text = nFormat.format(it)
        })

    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_confirm -> {
                update(saleDetail, view)
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
        binding.textViewClient.text = saleDetail.client?.name
        binding.textViewPayment.text = saleDetail.formPayment?.name
        binding.textViewSaleDate.text = df.format(saleDetail.salesDate)

        viewModelDetailSale.getTotalSale(saleDetail.saleServices.toMutableList(), saleDetail.saleProducts.toMutableList())

            binding.recyclerView.apply {
                adapter = AdapterAccountReceivableDetail(saleDetail.accounts.filter {
                val calendar = Calendar.getInstance()
                calendar.time = it.dueDate
                    calendar.get(Calendar.YEAR) == year &&
                    calendar.get(Calendar.MONTH) + 1 == month
                    })
                layoutManager = LinearLayoutManager(context)
            }

    }
    private fun update(sale: Sale, view: View?) {
//        lifecycleScope.launch {
//            viewModel.update(sale).collectLatest { state ->
//                when (state) {
//                    is State.Loading -> {
//                        binding.progressBar.visibility = View.VISIBLE
//                    }
//
//                    is State.Success -> {
//                        binding.progressBar.visibility = View.GONE
//                        view?.findNavController()?.navigate(R.id.nav_account_receivable)
//                    }
//
//                    is State.Failed -> {
//                        binding.progressBar.visibility = View.GONE
//                        Toast.makeText(
//                            activity, state.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
    }
}