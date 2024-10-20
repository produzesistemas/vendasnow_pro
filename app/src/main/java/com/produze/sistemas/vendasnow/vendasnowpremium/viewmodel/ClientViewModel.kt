package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.ClientRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import kotlinx.coroutines.*
class ClientViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<ResponseBody>()
    val errorMessage: LiveData<ResponseBody>
        get() = _errorMessage

    private val retrofitService = RetrofitService.getInstance()
    private val clientRepository = ClientRepository(retrofitService)
    private var responseBody: ResponseBody = ResponseBody()
    val itemButtonClickEvent: MutableLiveData<Client> by lazy {
        MutableLiveData<Client>()
    }

    val itemButtonClickEventEdit: MutableLiveData<ResponseBody> by lazy {
        MutableLiveData<ResponseBody>()
    }

    val lst = MutableLiveData<List<Client>>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()

    fun getAll(token: String) {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = clientRepository.getAll(token)) {
                is NetworkState.Success -> {
                    lst.postValue(response.data!!)
                    loading.value = false
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                        loading.value = false
                        onError("Sessão expirada! Para sua segurança efetue novamente o login.", 401)
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        onError("Falha na tentativa.", 400)
                    }

                    if (response.response.code() == 600) {
                        loading.value = false
                        onError("", 600)
                    }
                }
            }
        }
    }

    fun setCompleteFalse() {
        complete.value = false
    }

    fun save(client: Client, token: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = clientRepository.save(token, client)) {
                is NetworkState.Success -> {
                    complete.value = true
                    loading.value = false
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                        loading.value = false
                        onError("Sessão expirada! Para sua segurança efetue novamente o login.", 401)
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        onError("Falha na tentativa.", 400)
                    }
                }
            }
        }
    }

    fun delete(client: Client, token: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = clientRepository.delete(token, client)) {
                is NetworkState.Success -> {
                    complete.value = true
                    loading.value = false
//                    itemButtonClickEvent.postValue(client)
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                        loading.value = false
                        onError("Sessão expirada! Para sua segurança efetue novamente o login.", 401)
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        onError("Falha na tentativa.", 400)
                    }
                }
            }
        }
    }

    fun onItemButtonClick(client: Client) {
        itemButtonClickEvent.postValue(client)
    }

    fun onItemButtonClickEdit(client: ResponseBody) {
        itemButtonClickEventEdit.postValue(client)
    }

    private fun onError(message: String, code: Int) {
        responseBody.code = code
        responseBody.message = message
        _errorMessage.postValue(responseBody)
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}