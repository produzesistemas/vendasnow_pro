package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.R.attr
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewAccountReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailSale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.widget.TableLayout
import android.view.Gravity

import android.R.attr.textSize

import android.util.TypedValue

import android.widget.TextView







class AdapterAccountReceivable (private val lst: List<Sale>, var viewModel: ViewModelAccountReceivable, var viewModelDetailSale: ViewModelDetailSale) :
    RecyclerView.Adapter<AdapterAccountReceivable.RecyclerViewViewHolder>(), Filterable {

    private lateinit var sale: Sale
    private var lstFilter: List<Sale> = arrayListOf()
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var df = SimpleDateFormat("dd/MM/yyyy")
    private var mTableLayout: TableLayout? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewAccountReceivableBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_view_account_receivable,
            parent,
            false)

        val tv2 = TextView(parent.context)

        return RecyclerViewViewHolder(binding)
    }

    init {
        lstFilter = lst
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lstFilter, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewAccountReceivableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<Sale>, position: Int) {

//            val dt = lst[position].salesDate?.let { it }
//            binding.textViewSalesDate.text = df.format(dt)
            binding.textViewClient.text = lst[position].client?.name
            binding.textViewPayment.text = lst[position].formPayment?.name
            binding.viewDetail.setBackgroundColor(itemView.getResources().getColor(R.color.purple))

            lst[position].accounts.forEach {

            }

            binding.cardViewSale.setOnClickListener {
                sale = lst[position]
                viewModelDetailSale.select(sale)
                when (it.id) {
                    R.id.cardViewSale -> it?.findNavController()?.navigate(R.id.nav_detail_sale)
                }
            }

//            binding.textViewTotal.text = nFormat.format(getTotalSale(lst[position].saleServices.toMutableList(), lst[position].saleProducts.toMutableList()))

        }

    }

    override fun getItemCount(): Int {
        return lstFilter.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    lstFilter = lst
                } else {
                    val resultList = ArrayList<Sale>()
                    for (row in lst) {
                        if (row.client?.name?.contains(charSearch, true)!!
                        ) {
                            resultList.add(row)
                        }
                    }
                    lstFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = lstFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                lstFilter = results?.values as List<Sale>
                notifyDataSetChanged()
                viewModel.getTotalByFilter(lstFilter)
            }

        }
    }

    fun getItems(): List<Sale> {
        return lstFilter
    }

    fun getTotalSale(lstService: MutableList<SaleService>, lstProduct: MutableList<SaleProduct>): Double {
        var total: Double = 0.00
        lstService.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        lstProduct.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        return total
    }

}