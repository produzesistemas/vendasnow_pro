package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewPlanBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Plan
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.SubscriptionViewModel
import java.text.NumberFormat
import java.util.*

class AdapterPlan(private val lst: List<Plan>, var viewModel: SubscriptionViewModel) :
        RecyclerView.Adapter<AdapterPlan.RecyclerViewViewHolder>() {

    private lateinit var plan: Plan
    val nFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewPlanBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_plan,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewPlanBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: List<Plan>, position: Int) {
            binding.textViewLabel.text = lst[position].description
            binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_green)
            binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.green))
            binding.imageView.setColorFilter(itemView.resources.getColor(R.color.green))
//
//            binding.cardView.setOnClickListener {
//
//            }
        }

    }

    override fun getItemCount(): Int {
        return lst.size
    }


}