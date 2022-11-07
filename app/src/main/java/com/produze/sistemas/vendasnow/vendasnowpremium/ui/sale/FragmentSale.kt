package com.produze.sistemas.vendasnow.vendasnowpremium.ui.sale

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentSaleBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import java.text.NumberFormat
import java.util.*

class FragmentSale : Fragment() {
    private val viewModelDetailSale: ViewModelDetailSale by activityViewModels()
    private lateinit var viewModel: SaleViewModel
    private lateinit var viewModelClient: ClientViewModel
    private lateinit var viewModelProductService: ProductViewModel
    private lateinit var binding: FragmentSaleBinding
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterSale: AdapterSale
    private lateinit var calendar: GregorianCalendar
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private var mSearchItem: MenuItem? = null
    private var sv: SearchView? = null
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    private var filter: FilterDefault = FilterDefault()

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
        setHasOptionsMenu(true);
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {
            changeActivity()
        } else {
            if (token.subscriptionDate?.let { MainUtils.checkSubscription(it) } == true) {
                view?.findNavController()?.navigate(R.id.nav_subscription)
            }
        }
        viewModel = ViewModelProvider(this).get(SaleViewModel::class.java)
        adapterSale = AdapterSale(arrayListOf(), viewModel, viewModelDetailSale)
        viewModelClient = ViewModelProvider(this).get(ClientViewModel::class.java)
        viewModelProductService = ViewModelProvider(this).get(ProductViewModel::class.java)

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_sale))

        viewModel.totalSaleByFilter.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalSale.text = nFormat.format(it)
        })

        viewModel.lst.observe(this) {
            adapterSale  = AdapterSale(it, viewModel, viewModelDetailSale)
                        binding.recyclerView.apply {
                            adapter = adapterSale
                            layoutManager = LinearLayoutManager(context)
                        }
            binding.progressBar.visibility = View.GONE
        }

        viewModel.errorMessage.observe(this) {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401) {
                changeActivity()
            }

        }

        viewModel.loading.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.complete.observe(this, Observer {
            if (it) {
                calendar = GregorianCalendar()
                loadSales(calendar)
            }
        })

        binding.btnBack.setOnClickListener {
            calendar.add(Calendar.MONTH, -1);
            loadSales(calendar)
        }

        binding.btnGo.setOnClickListener {
            calendar.add(Calendar.MONTH, 1);
            loadSales(calendar)
        }

        calendar = GregorianCalendar()
        loadSales(calendar)
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

    private fun loadSales(calendar: Calendar) {
        binding.textViewAno.text = calendar.get(Calendar.YEAR).toString()
        binding.textViewMes.text = MainUtils.getMonth(calendar.get(Calendar.MONTH) + 1)
        filter.month = calendar.get(Calendar.MONTH) + 1
        filter.year = calendar.get(Calendar.YEAR)
        viewModel.getAllByMonthAndYear(filter, token.token)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_searchview, menu)

        mSearchItem = menu.findItem(R.id.action_search)
        sv = MenuItemCompat.getActionView(mSearchItem) as SearchView
        sv!!.isIconified = true
        sv!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                adapterSale.filter.filter(query)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        val id = item!!.itemId
        //handle item clicks
        if (id == R.id.action_help){
            //do your action here, im just showing toast
            this.watchYoutubeVideo("HuLREHvuBB8")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun watchYoutubeVideo(id: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=$id")
        )
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
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