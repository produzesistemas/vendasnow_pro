package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewSaleBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.components.AlertDialogDelete
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailSale
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelSale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AdapterSale(private val lst: List<Sale>, var viewModel: ViewModelSale, var viewModelDetailSale: ViewModelDetailSale) :
        RecyclerView.Adapter<AdapterSale.RecyclerViewViewHolder>(), Filterable {

    private lateinit var sale: Sale
    private var lstFilter: List<Sale> = arrayListOf()
    val nFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var df = SimpleDateFormat("dd/MM/yyyy")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewSaleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_sale,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    init {
        lstFilter = lst
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lstFilter, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewSaleBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<Sale>, position: Int) {

            val dt = lst[position].salesDate?.let { it }
            binding.textViewSalesDate.text = df.format(dt)
            binding.textViewClient.text = lst[position].client?.name
            binding.textViewPayment.text = lst[position].formPayment?.name
            binding.viewDetail.setBackgroundColor(itemView.getResources().getColor(R.color.green))
            binding.btnDelete.setOnClickListener {
                sale = lst[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = sale?.let {
                    it.client?.let { it1 ->
                        AlertDialogDelete(it1.name) {
                            viewModel.onItemButtonClick(sale)
                        }
                    }
                }
                dialog?.show(manager, "dialog")
            }
            binding.cardViewSale.setOnClickListener {
                sale = lst[position]
                viewModelDetailSale.select(sale)
                when (it.id) {
                    R.id.cardViewSale -> it?.findNavController()?.navigate(R.id.nav_detail_sale)
                }
            }

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
            }

        }
    }


}