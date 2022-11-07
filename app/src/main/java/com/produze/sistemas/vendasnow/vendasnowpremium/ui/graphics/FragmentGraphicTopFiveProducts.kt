package com.produze.sistemas.vendasnow.vendasnowpremium.ui.graphics

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentGraphicsTopFiveProductsBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.SaleViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import java.util.*


class FragmentGraphicTopFiveProducts : Fragment(){

    private lateinit var binding: FragmentGraphicsTopFiveProductsBinding
    private lateinit var calendar: GregorianCalendar
    private lateinit var viewModel: SaleViewModel
    var mChart: PieChart? = null
    private var legend: Legend? = null
    private lateinit var viewModelMain: ViewModelMain
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
                R.layout.fragment_graphics_top_five_products,
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
            changeActivity()
        } else {
            if (token.subscriptionDate?.let { MainUtils.checkSubscription(it) } == true) {
                view?.findNavController()?.navigate(R.id.nav_subscription)
            }
        }
        viewModel = ViewModelProvider(this).get(SaleViewModel::class.java)
        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.label_graphics_top_products))

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        mChart = binding.chart
        legend = binding.chart.legend

        viewModel.lst.observe(this) {
            loadGraph(it.sortedWith(compareBy { it.saleDate }))
            binding.progressBar.visibility = View.GONE
        }

        viewModel.errorMessage.observe(this) {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401) {
                changeActivity()
            }

        }

        viewModel.loading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }


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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_cancel -> {
                view?.findNavController()?.navigate(R.id.nav_graphic)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    private fun loadGraph(sales: List<Sale>) {
        var entries: ArrayList<PieEntry> = ArrayList()
        var lst: MutableList<List<SaleProduct>> = sales.map { it.saleProduct }.toMutableList()
        val flattened: List<SaleProduct> = lst.flatten()
        flattened.forEach{
            entries.add(PieEntry(it.quantity.toFloat(), it.product?.name))
        }
        var result = entries
                .groupBy { it.label }
                .mapValues { entry -> entry.value.sumOf { it.value.toInt() } }.toList()

        entries = arrayListOf()

        result = result.sortedWith(compareByDescending { it.second }).take(5)
        result.forEach{
            entries.add(PieEntry(it.second.toFloat(), it.first))
        }

        val set = PieDataSet(entries, "")
        val data = PieData(set)

        val colors: MutableList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue))
        colors.add(ContextCompat.getColor(requireContext(), R.color.red))
        colors.add(ContextCompat.getColor(requireContext(), R.color.green))
        colors.add(ContextCompat.getColor(requireContext(), R.color.black))
        colors.add(ContextCompat.getColor(requireContext(), R.color.purple))

        set.colors = colors
        data.setValueTextSize(20f)

        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        mChart?.data = data
        mChart?.invalidate()
        mChart?.description?.isEnabled = false
        mChart?.setNoDataText("Nenhuma venda encontrada.")
        mChart?.setEntryLabelTextSize(14f)
        mChart?.setExtraOffsets(5f, 2f, 5f, 2f)
            mChart?.legend?.isEnabled = false
        mChart?.setEntryLabelColor(Color.BLACK);
        mChart?.setEntryLabelTextSize(10f);

    }

    private fun load(calendar: Calendar) {
        binding.textViewAno.text = calendar.get(Calendar.YEAR).toString()
        binding.textViewMes.text = MainUtils.getMonth(calendar.get(Calendar.MONTH) + 1)
        filter.year = calendar.get(Calendar.YEAR)
        filter.month = calendar.get(Calendar.MONTH) + 1
        viewModel.getAllByMonthAndYear(filter, token.token)
    }

    private fun changeActivity() {
        activity?.let{
            datasource!!.deleteAll()
            val intent = Intent (it, LoginActivity::class.java)
            it.startActivity(intent)
        }
    }
}