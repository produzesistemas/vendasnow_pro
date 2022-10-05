package com.produze.sistemas.vendasnow.vendasnowpremium.ui.account

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
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentAccountsReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class FragmentAccountReceivable : Fragment() {
    private lateinit var viewModelAccountReceivable: AccountReceivableViewModel
    private val viewModelDetailAccountReceivable: ViewModelDetailAccountReceivable by activityViewModels()
    private lateinit var viewModelDetailSale: ViewModelDetailSale
    private lateinit var viewModelClient: ClientViewModel
    private lateinit var viewModelProductService: ProductViewModel
    private lateinit var binding: FragmentAccountsReceivableBinding
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterAccountReceivable: AdapterAccountReceivable
    private var calendar: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
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
            R.layout.fragment_accounts_receivable,
            container,
            false
        )
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {

        }
        viewModelAccountReceivable = ViewModelProvider(this).get(AccountReceivableViewModel::class.java)
        viewModelDetailSale = ViewModelProvider(this).get(ViewModelDetailSale::class.java)
        adapterAccountReceivable = AdapterAccountReceivable(arrayListOf(), viewModelAccountReceivable, viewModelDetailAccountReceivable)
        viewModelClient = ViewModelProvider(this).get(ClientViewModel::class.java)
        viewModelProductService = ViewModelProvider(this).get(ProductViewModel::class.java)

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_accounts_receivable))

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        viewModelAccountReceivable.totalSaleByFilter.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalSale.text = nFormat.format(it)
        })

        viewModelAccountReceivable.lst.observe(this) {
            adapterAccountReceivable  = AdapterAccountReceivable(it.sortedWith(compareBy { it.dueDate }), viewModelAccountReceivable, viewModelDetailAccountReceivable)
                        binding.recyclerView.apply {
                            adapter = adapterAccountReceivable
                            layoutManager = LinearLayoutManager(context)
                        }

            binding.progressBar.visibility = View.GONE
        }

        viewModelAccountReceivable.errorMessage.observe(this, Observer {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401) {
                changeActivity()
            }

        })

        viewModelAccountReceivable.loading.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModelAccountReceivable.complete.observe(this, Observer {
            if (it) {
                calendar = GregorianCalendar()
                load(calendar)
            }
        })

        calendar = GregorianCalendar()
        load(calendar)

        binding.btnBack.setOnClickListener {
            calendar.add(Calendar.MONTH, -1);
            load(calendar)
        }

        binding.btnGo.setOnClickListener {
            calendar.add(Calendar.MONTH, 1);
            load(calendar)
        }
    }

    private fun load(calendar: Calendar) {
        binding.textViewAno.text = calendar.get(Calendar.YEAR).toString()
        binding.textViewMes.text = MainUtils.getMonth(calendar.get(Calendar.MONTH) + 1)
        filter.month = calendar.get(Calendar.MONTH) + 1
        filter.year = calendar.get(Calendar.YEAR)
        filter.status = 1
        viewModelAccountReceivable.getAllByMonthAndYear(filter, token.token)
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
                adapterAccountReceivable.filter.filter(query)
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
            this.watchYoutubeVideo("9fH90LauX_Q")
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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_account_to_receive -> {
                filter.month = MainUtils.getMonthInt(binding.textViewMes.text.toString())
                filter.year = Integer.parseInt(binding.textViewAno.text.toString())
                filter.status = 1
                binding.textViewLabelTotalAccounts.text = resources.getString(R.string.label_panel_account_receive)
                loadByFilter(filter)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_account_receive -> {
                filter.month = MainUtils.getMonthInt(binding.textViewMes.text.toString())
                filter.year = Integer.parseInt(binding.textViewAno.text.toString())
                filter.status = 2
                binding.textViewLabelTotalAccounts.text = resources.getString(R.string.label_panel_account_to_receive)
                loadByFilter(filter)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadByFilter(filter: FilterDefault) {
        viewModelAccountReceivable.getAllByMonthAndYear(filter, token.token)
    }

}