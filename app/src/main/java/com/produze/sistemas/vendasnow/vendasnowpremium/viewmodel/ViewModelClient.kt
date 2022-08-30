package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryClient
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.Response


class ViewModelClient : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<Client> by lazy {
        MutableLiveData<Client>()
    }

    val itemButtonClickEventEdit: MutableLiveData<ResponseBody> by lazy {
        MutableLiveData<ResponseBody>()
    }

    var repository = RepositoryClient()



    val clients: MutableLiveData<List<Client>> by lazy {
        MutableLiveData<List<Client>>()
    }

    val responseBodyClient: MutableLiveData<ResponseBody> by lazy {
        MutableLiveData<ResponseBody>()
    }

//    val clients : Flow<State<List<Client?>>> = repository.getAll()

    fun save(client: Client, token: String) = repository.save(client, token)
    fun getAll(email: String) = repository.getAll(email)
    fun delete(client: Client, token: String) = repository.delete(client, token)

    fun onItemButtonClick(client: Client) {
        itemButtonClickEvent.postValue(client)
    }

    fun onItemButtonClickEdit(client: ResponseBody) {
        itemButtonClickEventEdit.postValue(client)
    }

}