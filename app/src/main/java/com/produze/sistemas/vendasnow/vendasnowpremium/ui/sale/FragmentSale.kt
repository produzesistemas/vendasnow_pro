package com.produze.sistemas.vendasnow.vendasnowpremium.ui.sale

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentSaleBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class FragmentSale : Fragment() {
    private val viewModelDetailSale: ViewModelDetailSale by activityViewModels()
    private lateinit var viewModel: ViewModelSale
    private lateinit var viewModelClient: ViewModelClient
    private lateinit var viewModelProductService: ViewModelProduct
    private lateinit var binding: FragmentSaleBinding
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterSale: AdapterSale
    private lateinit var calendar: GregorianCalendar
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_sale,
                container,
                false
        )
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewModelSale::class.java)
        adapterSale = AdapterSale(arrayListOf(), viewModel, viewModelDetailSale)
        viewModelClient = ViewModelProvider(this).get(ViewModelClient::class.java)
        viewModelProductService = ViewModelProvider(this).get(ViewModelProduct::class.java)

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapterSale.filter.filter(newText)
                    return false
                }

            })
        }

        val observer = Observer<Sale> { sale ->
            lifecycleScope.launch {
                viewModel.delete(sale).collectLatest { state ->
                    when (state) {
                        is State.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is State.Success -> {
                            calendar = GregorianCalendar()
                            load(calendar)
                        }
                        is State.Failed -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(activity, state.message,
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        viewModel.itemButtonClickEvent.observe(viewLifecycleOwner, observer)

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_sale))

        viewModel.totalSaleByFilter.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalSale.text = nFormat.format(it)
        })

        calendar = GregorianCalendar()
        load(calendar)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_add -> {
                view?.findNavController()?.navigate(R.id.nav_new_sale)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun load(calendar: Calendar) {
        lifecycleScope.launch {
            viewModel.getAllByMonthAndYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1).collectLatest { state ->
                when (state) {
                    is State.Loading -> {

                    }
                    is State.Success -> {
                        adapterSale  = AdapterSale((state.data as MutableList<Sale>).sortedWith(compareBy { it.salesDate }), viewModel, viewModelDetailSale)
                        binding.recyclerView.apply {
                            adapter = adapterSale
                            layoutManager = LinearLayoutManager(context)
                        }
                        binding.progressBar.visibility = View.GONE
                        viewModel.getTotalByFilter((state.data as MutableList<Sale>).sortedWith(compareBy { it.salesDate }))
                    }

                    is State.Failed -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(activity, state.message,
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}