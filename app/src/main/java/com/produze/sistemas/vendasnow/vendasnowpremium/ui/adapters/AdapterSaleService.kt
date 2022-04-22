package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewSaleServiceBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.components.AlertDialogDelete
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelSaleService
import java.text.NumberFormat
import java.util.*

class AdapterSaleService(private var lst: List<SaleService>, var viewModelSaleService: ViewModelSaleService) :
        RecyclerView.Adapter<AdapterSaleService.RecyclerViewViewHolder>() {

    val nFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var saleService: SaleService
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewSaleServiceBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_sale_service,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewSaleServiceBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<SaleService>, position: Int) {
            binding.textViewName.text = lst[position].descricao
            binding.textViewValue.text = nFormat.format(lst[position].valueSale)
             binding.btnDelete.setOnClickListener {
                saleService = lst[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = saleService?.let {
                    it.let { it1 ->
                        AlertDialogDelete(it1.descricao) {
                            viewModelSaleService.onItemButtonClick(saleService)
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