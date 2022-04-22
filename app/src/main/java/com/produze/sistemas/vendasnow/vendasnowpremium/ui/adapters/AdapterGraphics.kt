package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewGraphicsBinding

class AdapterGraphics(private val lst: MutableList<String>) :
        RecyclerView.Adapter<AdapterGraphics.RecyclerViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewGraphicsBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_graphics,
                parent,
                false)
        return RecyclerViewViewHolder(binding)
    }

    init {

    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewGraphicsBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: MutableList<String>, position: Int) {
            binding.textViewLabel.text = lst[position]
            when (position) {
                0 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_purple)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.purple))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.purple))
                }
                1 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_purple)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.purple))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.purple))
                }
                2 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_purple)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.purple))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.purple))
                }
                3 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_purple)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.purple))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.purple))
                }
            }

            binding.cardView.setOnClickListener {
                when (position) {
                    0 -> it?.findNavController()?.navigate(R.id.nav_graphic_top_five_products)
                    1 -> it?.findNavController()?.navigate(R.id.nav_graphic_top_five_services)
                    2 -> it?.findNavController()?.navigate(R.id.nav_graphic_invoice_year)
                    3 -> it?.findNavController()?.navigate(R.id.nav_graphic_profit_year)
                 }
            }

        }

    }

    override fun getItemCount(): Int {
        return lst.size
    }

}