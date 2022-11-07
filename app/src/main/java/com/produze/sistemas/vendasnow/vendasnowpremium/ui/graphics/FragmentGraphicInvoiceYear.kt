package com.produze.sistemas.vendasnow.vendasnowpremium.ui.graphics

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentGraphicsInvoiceYearBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.SaleViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import java.text.NumberFormat
import java.util.*

class FragmentGraphicInvoiceYear : Fragment(){

    private lateinit var binding: FragmentGraphicsInvoiceYearBinding
    private lateinit var calendar: GregorianCalendar
    private lateinit var viewModel: SaleViewModel
    var mChart: BarChart? = null
    private lateinit var viewModelMain: ViewModelMain
    private var total: Float = 0.0f
    private var totalGeral: Float = 0.0f
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
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
                R.layout.fragment_graphics_invoice_year,
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
        viewModelMain.updateActionBarTitle(getString(R.string.label_graphics_invoices_year))

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mChart = binding.chart

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
            calendar.add(Calendar.YEAR, -1);
            load(calendar)
        }

        binding.btnGo.setOnClickListener {
            calendar.add(Calendar.YEAR, 1);
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
        totalGeral = 0.0f
        var entries: ArrayList<BarEntry> = ArrayList()
        for (mes in 12 downTo 1 step 1) {
            var salesByMonth = sales.filter {

                var m = it.saleDate?.month
                if (m != null) {
                    m += 1
                }
                m == mes
            }

            salesByMonth.forEach{
                it.saleProduct.forEach {
                    total += (it.valueSale.times(it.quantity))?.toFloat()!!
                }

                it.saleService.forEach {
                    total += (it.valueSale.times(it.quantity))?.toFloat()!!
                }
            }

            if (total > 0) {
                entries.add(BarEntry(mes.toFloat() - 1, total))
                totalGeral += total
                total = 0.0f
            }

        }

        binding.textViewTotal.text = nFormat.format(totalGeral)
        totalGeral = 0.0f

        mChart!!.invalidate()
        mChart!!.description.isEnabled = false
        mChart!!.setNoDataText("Nenhuma venda encontrada.")
        mChart!!.legend.isEnabled = false // Hide the legend
        mChart!!.axisRight.setDrawLabels(false)
        mChart?.setExtraOffsets(5f, 10f, 5f, 10f)

        val xAxis = mChart!!.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return MainUtils.getMonth(Math.round(value) + 1)
            }
        }

        val set1: BarDataSet = BarDataSet(entries, "")

        set1.values = entries

        val colors: MutableList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue))
        colors.add(ContextCompat.getColor(requireContext(), R.color.red))
        colors.add(ContextCompat.getColor(requireContext(), R.color.green))
        colors.add(ContextCompat.getColor(requireContext(), R.color.black))
        colors.add(ContextCompat.getColor(requireContext(), R.color.purple))
        set1.colors = colors
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)

        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return nFormat.format(value.toDouble())
            }
        })

        data.barWidth = 0.9f
        mChart!!.data = data

    }

    private fun load(calendar: Calendar) {
        binding.textViewAno.text = calendar.get(Calendar.YEAR).toString()
        filter.year = calendar.get(Calendar.YEAR)
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