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
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.ClientRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.LoginRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import kotlinx.coroutines.*
class LoginViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<ResponseBody>()
    val errorMessage: LiveData<ResponseBody>
        get() = _errorMessage

    private val retrofitService = RetrofitService.getInstance()
    private val loginRepository = LoginRepository(retrofitService)
    private var responseBody: ResponseBody = ResponseBody()

    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()
    val token = MutableLiveData<Token>()
    fun setCompleteFalse() {
        complete.value = false
    }

    fun login(email: String, secret: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = loginRepository.login(email, secret)) {
                is NetworkState.Success -> {
                    loading.value = false
                    token.value = response.data!!
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                        loading.value = false
                        onError("Sessão expirada! Para sua segurança efetue novamente o login.", 401)
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        onError(response.response.errorBody()!!.string(), 400)
                    }

                    if (response.response.code() == 600) {
                        loading.value = false
                        onError("", 600)
                    }
                }
            }
        }
    }

    fun register(email: String, secret: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = loginRepository.register(email, secret)) {
                is NetworkState.Success -> {
                    loading.value = false
                    msg.value = response.data!!
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                        loading.value = false
                        onError("Sessão expirada! Para sua segurança efetue novamente o login.", 401)
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        onError(response.response.errorBody()!!.string(), 400)
                    }
                }
            }
        }
    }

    fun forgot(email: String, secret: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = loginRepository.forgot(email, secret)) {
                is NetworkState.Success -> {
                    loading.value = false
                    msg.value = response.data!!
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                        loading.value = false
                        onError("Sessão expirada! Para sua segurança efetue novamente o login.", 401)
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        onError(response.response.errorBody()!!.string(), 400)
                    }
                }
            }
        }
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