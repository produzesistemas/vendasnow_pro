package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewMainBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils

class AdapterMain(private val lst: MutableList<String>, context: Context) :
    RecyclerView.Adapter<AdapterMain.RecyclerViewViewHolder>() {
    private var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder{
        val binding: CardViewMainBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_view_main,
            parent,
            false)
        return RecyclerViewViewHolder(binding)
    }

    init {
        mContext = context;
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(lst, position)
    }

    inner class RecyclerViewViewHolder(private val binding: CardViewMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lst: MutableList<String>, position: Int) {
            binding.textViewLabel.text = lst[position]
            when (position) {
                0 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_blue)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.blue))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.blue))
                }
                1 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_red)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.red))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.red))
                }
                2 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_green)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.green))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.green))
                }
                3 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_black)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.black))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.black))
                }
                4 -> {
                    binding.cardView.setBackgroundResource(R.drawable.custom_background_cardview_border_purple)
                    binding.textViewLabel.setTextColor(itemView.resources.getColor(R.color.purple))
                    binding.imageView.setColorFilter(itemView.resources.getColor(R.color.purple))
                }
            }

            binding.cardView.setOnClickListener {
                if (mContext?.let { it1 -> MainUtils.isOnline(it1) }!!) {
                    when (position) {
                        0 -> it?.findNavController()?.navigate(R.id.nav_client)
                        1 -> it?.findNavController()?.navigate(R.id.nav_product)
                        2 -> it?.findNavController()?.navigate(R.id.nav_sale)
                        3 -> it?.findNavController()?.navigate(R.id.nav_account_receivable)
                        4 -> it?.findNavController()?.navigate(R.id.nav_graphic)
                    }
                } else {
                    Toast.makeText(mContext, R.string.validation_connection,
                        Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    override fun getItemCount(): Int {
        return lst.size
    }

}