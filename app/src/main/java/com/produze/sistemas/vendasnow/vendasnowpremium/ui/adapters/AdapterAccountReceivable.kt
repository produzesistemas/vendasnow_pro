package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewAccountReceivableBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.AccountReceivableViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.widget.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelDetailAccountReceivable


class AdapterAccountReceivable (private val lst: List<Account>,
                                var viewModel: AccountReceivableViewModel,
                                var viewModelDetailAccountReceivable: ViewModelDetailAccountReceivable) :
    RecyclerView.Adapter<AdapterAccountReceivable.RecyclerViewViewHolder>(), Filterable {

    private lateinit var account: Account
    private var lstFilter: List<Account> = arrayListOf()
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var df = SimpleDateFormat("dd/MM/yyyy")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewAccountReceivableBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_view_account_receivable,
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

    inner class RecyclerViewViewHolder(private val binding: CardViewAccountReceivableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<Account>, position: Int) {

            val dt = lst[position].dueDate
            binding.textViewDueDate.text = df.format(dt)
            binding.textViewClient.text = lst[position].sale?.client!!.name
            binding.textViewValue.text = nFormat.format(lst[position].value)
            binding.viewDetail.setBackgroundColor(itemView.resources.getColor(R.color.purple))

            binding.cardViewSale.setOnClickListener {
                account = lst[position]
                viewModelDetailAccountReceivable.selectedAccount(account)
//                viewModelDetailAccountReceivable.selectedYear(year)
//                viewModelDetailAccountReceivable.selectedMonth(month)
                when (it.id) {
                    R.id.cardViewSale -> it?.findNavController()?.navigate(R.id.nav_detail_account_receivable)
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
                    val resultList = ArrayList<Account>()
                    for (row in lst) {
                        if (row.sale?.client?.name?.contains(charSearch, true)!!
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
                lstFilter = results?.values as List<Account>

                notifyDataSetChanged()
                viewModel.getTotalByFilter(lstFilter)
            }

        }
    }

    fun getItems(): List<Account> {
        return lstFilter
    }

}