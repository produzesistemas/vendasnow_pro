package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewAccountBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AdapterAccountReceivableDetail (private var lst: List<Account>) :
    RecyclerView.Adapter<AdapterAccountReceivableDetail.RecyclerViewViewHolder>() {

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
            binding.textViewSituation.text = getStatusName(lst[position].status, itemView)
            binding.textViewValue.text = nFormat.format(lst[position].value)
            binding.viewDetail.setBackgroundColor(itemView.getResources().getColor(R.color.purple))

            when (lst[position].status) {
                1 -> {binding.radioGroup.check(R.id.radioButtonToReceive)}
                2 -> {binding.radioGroup.check(R.id.radioButtonReceive)}
            }

            binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radioButtonToReceive -> {
                        lst[position].status = 1
                        notifyDataSetChanged()
                    }
                    R.id.radioButtonReceive -> {
                        lst[position].status = 2
                        notifyDataSetChanged()
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    fun getStatusName(status: Int, view: View) : String{
        var str = ""
        val arrayStatus = view.getResources().getStringArray(R.array.ArrayStatus)

        arrayStatus.forEach {
            val s = it.split(",")
            if (status === s[0].toInt()) {
                str = s[1]
            }
        }
        return str
    }

}