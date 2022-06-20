package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryClient
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterClient
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class ViewModelClient : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<Client> by lazy {
        MutableLiveData<Client>()
    }

    val itemButtonClickEventEdit: MutableLiveData<Client> by lazy {
        MutableLiveData<Client>()
    }

    var repository = RepositoryClient()

    fun getAll(token: String) {
        viewModelScope.launch {
            try {
                clients.postValue(repository.getAll(token))
//                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.message?.let { Log.e("CarrierViewModel", it) }
            }

        }
    }

    val clients: MutableLiveData<List<Client>> by lazy {
        MutableLiveData<List<Client>>()
    }

//    val clients : Flow<State<List<Client?>>> = repository.getAll()

    fun add(client: Client, email: String) = repository.add(client, email)

    fun update(client: Client) = repository.update(client)

    fun delete(client: Client) = repository.delete(client)

    fun onItemButtonClick(client: Client) {
        itemButtonClickEvent.postValue(client)
    }

    fun onItemButtonClickEdit(client: Client) {
        itemButtonClickEventEdit.postValue(client)
    }

}