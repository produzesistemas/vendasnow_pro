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
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewProductBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.components.AlertDialogDelete
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.product.DialogNewProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelProduct
import java.text.NumberFormat
import java.util.*

class AdapterProduct(private val lst: List<Product>, var viewModel: ViewModelProduct) :
        RecyclerView.Adapter<AdapterProduct.RecyclerViewViewHolder>(), Filterable {

    private lateinit var product: Product
    private var productsFilter: List<Product> = arrayListOf()
    val nFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewProductBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_product,
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

    inner class RecyclerViewViewHolder(private val binding: CardViewProductBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<Product>, position: Int) {
            binding.textViewName.text = lst[position].name
            binding.textViewValue.text = nFormat.format(lst[position].value)
            binding.textViewCostValue.text = nFormat.format(lst[position].costValue)

            binding.viewDetail.setBackgroundColor(itemView.getResources().getColor(R.color.red))
            binding.btnDelete.setOnClickListener {
                product = lst[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = product?.let {
                    AlertDialogDelete(it.name) {
                        viewModel.onItemButtonClick(product)
                    }
                }
                dialog?.show(manager, "dialog")
            }

            binding.btnEdit.setOnClickListener {
                product = lst[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = product?.let {
                    DialogNewProduct(viewModel, it) {
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
                    val resultList = ArrayList<Product>()
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
                productsFilter = results?.values as List<Product>
                notifyDataSetChanged()
            }

        }
    }




}