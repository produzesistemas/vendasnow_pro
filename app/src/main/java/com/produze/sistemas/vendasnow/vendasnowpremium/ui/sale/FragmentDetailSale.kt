package com.produze.sistemas.vendasnow.vendasnowpremium.ui.sale

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentDetailSaleBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSaleProductDetail
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSaleServiceDetail
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailSale
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentDetailSale : Fragment(){
    private val viewModelDetailSale: ViewModelDetailSale by activityViewModels()
    private lateinit var binding: FragmentDetailSaleBinding
    var df = SimpleDateFormat("dd/MM/yyyy")
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_detail_sale,
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
        viewModelMain.updateActionBarTitle(getString(R.string.label_detail_sale))
        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        viewModelDetailSale.selected.observe(viewLifecycleOwner, Observer<Sale> { item ->
            load(item)
        })

        viewModelDetailSale.totalProducts.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalProducts.text = nFormat.format(it)
        })

        viewModelDetailSale.totalServices.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalServices.text = nFormat.format(it)
        })

        viewModelDetailSale.totalSale.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalSale.text = nFormat.format(it)
        })

        viewModelDetailSale.totalProfit.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewProductsProfit.text = nFormat.format(it)
        })
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_cancel -> {
                view?.findNavController()?.navigate(R.id.nav_sale)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun load(sale: Sale) {
        binding.textViewClient.text = sale.client?.name
        binding.textViewPayment.text = sale.formPayment?.name
        binding.textViewSaleDate.text = df.format(sale.salesDate)
        if (sale.saleProducts.isEmpty()) {
            binding.ConstraintLayoutProducts.visibility = View.GONE
        } else {
            binding.recyclerViewProducts.apply {
                adapter = AdapterSaleProductDetail(sale.saleProducts)
                layoutManager = LinearLayoutManager(context)
            }
            viewModelDetailSale.getTotalProducts(sale.saleProducts.toMutableList())
        }
        if (sale.saleServices.isEmpty()) {
            binding.ConstraintLayoutServices.visibility = View.GONE
        } else {
            binding.recyclerViewServices.apply {
                adapter = AdapterSaleServiceDetail(sale.saleServices)
                layoutManager = LinearLayoutManager(context)
            }
            viewModelDetailSale.getTotalServices(sale.saleServices.toMutableList())
        }
        viewModelDetailSale.getTotalSale(sale.saleServices.toMutableList(), sale.saleProducts.toMutableList())
        viewModelDetailSale.getTotalProfit(sale.saleProducts.toMutableList())
    }

}