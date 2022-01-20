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
import androidx.navigation.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentGraphicsTopFiveServicesBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelSale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class FragmentGraphicTopFiveServices : Fragment(){

    private lateinit var binding: FragmentGraphicsTopFiveServicesBinding
    private lateinit var calendar: GregorianCalendar
    private lateinit var viewModel: ViewModelSale
    var mChart: PieChart? = null
    private lateinit var viewModelMain: ViewModelMain

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_graphics_top_five_services,
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
        viewModelMain.updateActionBarTitle(getString(R.string.label_graphics_top_services))
        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mChart = binding.chart
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
        var lst: MutableList<List<SaleService>> = sales.map { it.saleServices }.toMutableList()
        val flattened: List<SaleService> = lst.flatten()
        flattened.forEach{
            entries.add(PieEntry(it.quantity.toFloat(), it.service?.name))
        }
        var result = entries
                .groupBy { it.label }
                .mapValues { entry -> entry.value.sumBy { it.value.toInt() } }.toList()

        entries = arrayListOf()

        result = result.sortedWith(compareByDescending { it.second }).take(5)
        result.forEach{
            entries.add(PieEntry(it.second.toFloat(), it.first))
        }

        val set = PieDataSet(entries, "")
        val data = PieData(set)

        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })


        val colors: MutableList<Int> = ArrayList()
        colors.add(ContextCompat.getColor(requireContext(), R.color.blue))
        colors.add(ContextCompat.getColor(requireContext(), R.color.red))
        colors.add(ContextCompat.getColor(requireContext(), R.color.green))
        colors.add(ContextCompat.getColor(requireContext(), R.color.black))
        colors.add(ContextCompat.getColor(requireContext(), R.color.purple))

        set.colors = colors
        data.setValueTextSize(20f)
        mChart?.setData(data)
        mChart?.invalidate()
        mChart?.getDescription()?.setEnabled(false)
        mChart?.setNoDataText("Nenhuma venda encontrada.")
        mChart?.setEntryLabelTextSize(14f)
        mChart?.setEntryLabelColor(Color.TRANSPARENT)
        mChart?.setExtraOffsets(5f, 5f, 5f, 5f)

        val l: Legend? = mChart?.legend
        if (l != null) {
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.form = Legend.LegendForm.CIRCLE
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 15f
            l.xOffset = 15f
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