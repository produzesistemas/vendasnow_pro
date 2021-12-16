package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewAccountBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AdapterAccount (private val lst: List<Account>) :
    RecyclerView.Adapter<AdapterAccount.RecyclerViewViewHolder>() {

    private lateinit var sale: Sale
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var df = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewAccountBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_view_account,
            parent,
            false)

        return RecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<Account>, position: Int) {

            val dt = lst[position].dueDate?.let { it }
            binding.textViewDueDate.text = df.format(dt)

        }

    }

    override fun getItemCount(): Int {
        return lst.size
    }



}