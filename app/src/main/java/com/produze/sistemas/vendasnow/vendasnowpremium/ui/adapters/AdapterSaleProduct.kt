package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewSaleProductBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.components.AlertDialogDelete
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelSaleProduct
import java.text.NumberFormat
import java.util.*

class AdapterSaleProduct(private var lst: List<SaleProduct>, var viewModelSaleProduct: ViewModelSaleProduct) :
        RecyclerView.Adapter<AdapterSaleProduct.RecyclerViewViewHolder>() {

    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var saleProduct: SaleProduct
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewSaleProductBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_sale_product,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewSaleProductBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<SaleProduct>, position: Int) {
            binding.textViewName.text = lst[position].product?.name
            binding.textViewValue.text = nFormat.format(lst[position].valueSale)
            binding.textViewQuantity.text = lst[position].quantity.toInt().toString()
            binding.textViewSubtotal.text = nFormat.format((lst[position].valueSale.times(lst[position].quantity)))
            binding.btnDelete.setOnClickListener {
                saleProduct = lst[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = saleProduct?.let {
                    it.product?.let { it1 ->
                        AlertDialogDelete(it1.name) {
                            viewModelSaleProduct.onItemButtonClick(saleProduct)
                        }
                    }
                }
                dialog?.show(manager, "dialog")
            }
        }

    }

    override fun getItemCount(): Int {
        return lst.size
    }

}