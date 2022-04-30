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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentAccountsReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentSaleBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*


class FragmentAccountReceivable : Fragment() {
    private lateinit var viewModelAccountReceivable: ViewModelAccountReceivable
    private val viewModelDetailAccountReceivable: ViewModelDetailAccountReceivable by activityViewModels()
    private lateinit var viewModelDetailSale: ViewModelDetailSale
    private lateinit var viewModelClient: ViewModelClient
    private lateinit var viewModelProductService: ViewModelProduct
    private lateinit var binding: FragmentAccountsReceivableBinding
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterAccountReceivable: AdapterAccountReceivable
    private var calendar: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private var mSearchItem: MenuItem? = null
    private var sv: SearchView? = null

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
        setHasOptionsMenu(true);
        viewModelAccountReceivable = ViewModelProvider(this).get(ViewModelAccountReceivable::class.java)
        viewModelDetailSale = ViewModelProvider(this).get(ViewModelDetailSale::class.java)
        adapterAccountReceivable = AdapterAccountReceivable(arrayListOf(), viewModelAccountReceivable, viewModelDetailAccountReceivable, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        viewModelClient = ViewModelProvider(this).get(ViewModelClient::class.java)
        viewModelProductService = ViewModelProvider(this).get(ViewModelProduct::class.java)

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_accounts_receivable))

        viewModelAccountReceivable.totalSaleByFilter.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalSale.text = nFormat.format(it)
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
        lifecycleScope.launch {
            viewModelAccountReceivable.getAllByMonthAndYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1).collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is State.Success -> {
                        adapterAccountReceivable  = AdapterAccountReceivable((state.data as MutableList<Sale>).sortedWith(compareBy { it.salesDate }), viewModelAccountReceivable, viewModelDetailAccountReceivable, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
                        binding.recyclerView.apply {
                            adapter = adapterAccountReceivable
                            layoutManager = LinearLayoutManager(context)
                        }
                        binding.progressBar.visibility = View.GONE
                        viewModelAccountReceivable.getTotalByFilter((state.data as MutableList<Sale>).sortedWith(compareBy { it.salesDate }), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
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
            this.watchYoutubeVideo(R.string.movie_account.toString())
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

}