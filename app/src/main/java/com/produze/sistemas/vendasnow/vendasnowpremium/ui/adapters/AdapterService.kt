package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewServiceBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Service
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.components.AlertDialogDelete
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.service.DialogNewService
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelService
import java.text.NumberFormat
import java.util.*

class AdapterService(private val lst: List<Service>, var viewModel: ViewModelService) :
        RecyclerView.Adapter<AdapterService.RecyclerViewViewHolder>(), Filterable {

    private lateinit var service: Service
    private var productsFilter: List<Service> = arrayListOf()
    val nFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewServiceBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_service,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    init {
        productsFilter = lst
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(productsFilter, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewServiceBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<Service>, position: Int) {
            binding.textViewName.text = lst[position].name
            binding.textViewValue.text = nFormat.format(lst[position].value)
            binding.viewDetail.setBackgroundColor(itemView.getResources().getColor(R.color.black))
            binding.btnDelete.setOnClickListener {
                service = lst[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = service?.let {
                    AlertDialogDelete(it.name) {
                        viewModel.onItemButtonClick(service)
                    }
                }
                dialog?.show(manager, "dialog")
            }

            binding.btnEdit.setOnClickListener {
                service = lst[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = service?.let {
                    DialogNewService(viewModel, it) {
                        viewModel.onItemButtonClickEdit(it)
                    }
                }
                dialog?.show(manager, "dialog")
            }
        }

    }

    override fun getItemCount(): Int {
        return productsFilter.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    productsFilter = lst
                } else {
                    val resultList = ArrayList<Service>()
                    for (row in lst) {
                        if (row.name.contains(charSearch, true)
                        ) {
                            resultList.add(row)
                        }
                    }
                    productsFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = productsFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productsFilter = results?.values as List<Service>
                notifyDataSetChanged()
            }

        }
    }

}