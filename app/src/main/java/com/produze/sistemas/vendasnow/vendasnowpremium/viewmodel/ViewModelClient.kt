package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryClient


class ViewModelClient : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<Client> by lazy {
        MutableLiveData<Client>()
    }

    val itemButtonClickEventEdit: MutableLiveData<Client> by lazy {
        MutableLiveData<Client>()
    }

    var repository = RepositoryClient()

    fun getAll() = repository.getAll()

//    val clients : Flow<State<List<Client?>>> = repository.getAll()

    fun add(client: Client) = repository.add(client)

    fun update(client: Client) = repository.update(client)

    fun delete(client: Client) = repository.delete(client)

    fun onItemButtonClick(client: Client) {
        itemButtonClickEvent.postValue(client)
    }

    fun onItemButtonClickEdit(client: Client) {
        itemButtonClickEventEdit.postValue(client)
    }

}