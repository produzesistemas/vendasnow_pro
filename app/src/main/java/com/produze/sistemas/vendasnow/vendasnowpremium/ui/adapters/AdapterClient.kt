package com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.CardViewClientBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.client.AlertDialogDeleteClient
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.client.DialogNewClient
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.components.AlertDialogDelete
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ClientViewModel
import kotlin.collections.ArrayList


class AdapterClient(private var clients: List<Client>, var viewModel: ClientViewModel) :
        RecyclerView.Adapter<AdapterClient.RecyclerViewViewHolder>(), Filterable {

    private lateinit var client: Client
    private var clientsFilter: List<Client> = arrayListOf()
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val binding: CardViewClientBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.card_view_client,
                parent,
                false)

        return RecyclerViewViewHolder(binding)
    }

    init {
        clientsFilter = clients
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.bind(clientsFilter, position)
    }

    inner class RecyclerViewViewHolder(
        private val binding: CardViewClientBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(clients: List<Client>, position: Int) {
            binding.textViewName.text = clients[position].name
            binding.textViewTelephone.text = clients[position].telephone
            binding.viewDetail.setBackgroundColor(itemView.getResources().getColor(R.color.blue))
            binding.btnDelete.setOnClickListener {
                client = clients[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                val dialog = AlertDialogDeleteClient(viewModel, client)
                dialog?.show(manager, "dialog")
            }

            binding.btnEdit.setOnClickListener {
                client = clients[position]
                val manager: FragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
//                val dialog = client?.let {
//                    DialogNewClient(viewModel, it) {
//                      viewModel.onItemButtonClickEdit(it)
//                    }
//                }
//                dialog?.show(manager, "dialog")
            }
        }

    }

    override fun getItemCount(): Int {
        return clients.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    clientsFilter = clients
                } else {
                    val resultList = ArrayList<Client>()
                    for (row in clients) {
                        if (row.name.contains(charSearch, true)
                        ) {
                            resultList.add(row)
                        }
                    }
                    clientsFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = clientsFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clientsFilter = results?.values as List<Client>
                notifyDataSetChanged()
            }

        }
    }

}