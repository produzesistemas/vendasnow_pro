package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewSaleServiceDetailBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService
import java.text.NumberFormat
import java.util.*

class AdapterSaleServiceDetail(private var lst: List<SaleService>) :
        RecyclerView.Adapter<AdapterSaleServiceDetail.RecyclerViewViewHolder>() {

    val nFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewSaleServiceDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_sale_service_detail,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewSaleServiceDetailBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<SaleService>, position: Int) {
            binding.textViewName.text = lst[position].service?.name
            binding.textViewValue.text = nFormat.format(lst[position].valueSale)
            binding.textViewQuantity.text = lst[position].quantity.toString()
            binding.textViewSubtotal.text = nFormat.format((lst[position].valueSale.times(lst[position].quantity)))
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

}