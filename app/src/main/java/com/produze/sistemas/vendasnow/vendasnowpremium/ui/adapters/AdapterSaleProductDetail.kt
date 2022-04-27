package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewSaleProductDetailBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import java.text.NumberFormat
import java.util.*

class AdapterSaleProductDetail(private var lst: List<SaleProduct>) :
        RecyclerView.Adapter<AdapterSaleProductDetail.RecyclerViewViewHolder>() {

    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewSaleProductDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_sale_product_detail,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewSaleProductDetailBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<SaleProduct>, position: Int) {
            binding.textViewName.text = lst[position].product?.name
            binding.textViewValue.text = nFormat.format(lst[position].valueSale)
            binding.textViewQuantity.text = lst[position].quantity.toInt().toString()
            binding.textViewSubtotal.text = nFormat.format((lst[position].valueSale.times(lst[position].quantity)))
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

}