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

    val itemButtonClickEventEdit: MutableLiveData<Client> by lazy {
        MutableLiveData<Client>()
    }

    var repository = RepositoryClient()

    fun getAll(token: String) {
//        viewModelScope.launch {
//            repository.getAll(token).collectLatest { state ->
//                when (state) {
//                    is State.Loading -> {
//
//                    }
//                    is State.Success -> {
//                        clients.postValue(state.data)
//                    }
//
//                    is State.Failed -> {
//
//                    }
//                }
//            }
//
//        }
        viewModelScope.launch {
            try {
                clients.postValue(repository.getAll(token))
            } catch (e: Exception) {
                e.message?.let { Log.e("CarrierViewModel", it) }
            }

        }
    }

    val clients: MutableLiveData<List<Client>> by lazy {
        MutableLiveData<List<Client>>()
    }

    val responseBodyClient: MutableLiveData<ResponseBody> by lazy {
        MutableLiveData<ResponseBody>()
    }

//    val clients : Flow<State<List<Client?>>> = repository.getAll()

    fun add(client: Client, token: String) = repository.insert(client, token)

//    suspend fun add(client: Client, token: String) = repository.add(client, token)
//suspend fun getAll(email: String) = repository.getAll(email)
//    fun add(client: Client, token: String) {
//
//        viewModelScope.launch {
//            try {
//                responseBodyClient.postValue(repository.add(client, token))
//            } catch (e: Exception) {
//                e.message?.let { Log.e("CarrierViewModel", it) }
//            }
//        }
//    }


//        viewModelScope.launch {
//            try {
//                repository.add(client, token)
//                responseBodyClient.postValue(ResponseBody("", 200, arrayListOf(), null))
//            } catch (e: Exception) {
//                responseBodyClient.postValue(ResponseBody("", 400, arrayListOf(), null))
//            }
//
//        }
//    }

//    fun update(client: Client) = repository.update(client)
//
//    fun delete(client: Client) = repository.delete(client)

    fun onItemButtonClick(client: Client) {
        itemButtonClickEvent.postValue(client)
    }

    fun onItemButtonClickEdit(client: Client) {
        itemButtonClickEventEdit.postValue(client)
    }

}