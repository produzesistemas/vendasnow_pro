package com.produze.sistemas.vendasnow.vendasnowpremium.ui.graphics

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentGraphicsProductsProfitableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentGraphicsTopFiveProductsBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelSale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class FragmentGraphicTopFiveProductsProfitable : Fragment(){

    private lateinit var binding: FragmentGraphicsProductsProfitableBinding
    private lateinit var calendar: GregorianCalendar
    private lateinit var viewModel: ViewModelSale
    var mChart: BarChart? = null
    private lateinit var viewModelMain: ViewModelMain

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_graphics_products_profitable,
                container,
                false
        )

        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(ViewModelSale::class.java)
        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.label_graphics_top_products))

        mChart = binding.barChart
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

    fun loadGraph(sales: List<Sale>) {
        var entries: ArrayList<BarEntry> = ArrayList()
        var lst: MutableList<List<SaleProduct>> = sales.map { it.saleProducts }.toMutableList()

        val flattened: List<SaleProduct> = lst.flatten()
        flattened.forEach{
            entries.add(BarEntry(it.product?.costValue?.times(it.quantity)?.toFloat()!!, it.product?.value?.times(it.quantity)?.toFloat()!!))
        }
//        var result = entries
//            .groupBy { it.x }
//            .mapValues { entry -> entry.value.sumOf { it.value.toInt() } }.toList()



        var result = entries.sortedWith(compareByDescending { it.x }).take(5)

        entries = arrayListOf()
        result.forEach{
            entries.add(BarEntry(it.x.toFloat(), it.y))
        }

        mChart!!.invalidate()
        mChart!!.description.isEnabled = false
        mChart!!.setNoDataText("Nenhuma venda encontrada.")
        mChart!!.legend.isEnabled = false // Hide the legend
        mChart!!.axisRight.setDrawLabels(false)
        mChart?.setExtraOffsets(5f, 20f, 5f, 70f)

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
//        data.setValueFormatter(object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return nFormat.format(value.toDouble())
//            }
//        })

        data.barWidth = 0.9f
        mChart!!.data = data

        val l: Legend? = mChart?.legend
        if (l != null) {
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.form = Legend.LegendForm.CIRCLE
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 15f
            l.isWordWrapEnabled = true
            l.calculatedLineSizes
       }

    }

    private fun load(calendar: Calendar) {
        binding.textViewAno.text = calendar.get(Calendar.YEAR).toString()
        binding.textViewMes.text = MainUtils.getMonth(calendar.get(Calendar.MONTH) + 1)
        lifecycleScope.launch {
            viewModel.getAllByMonthAndYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1).collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is State.Success -> {
                        loadGraph((state.data as MutableList<Sale>).sortedWith(compareBy { it.salesDate }))
                        binding.progressBar.visibility = View.GONE
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