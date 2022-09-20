package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.ProductRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import kotlinx.coroutines.*
class ProductViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<ResponseBody>()
    val errorMessage: LiveData<ResponseBody>
        get() = _errorMessage

    private val retrofitService = RetrofitService.getInstance()
    private val productRepository = ProductRepository(retrofitService)
    private var responseBody: ResponseBody = ResponseBody()
    val itemButtonClickEvent: MutableLiveData<Product> by lazy {
        MutableLiveData<Product>()
    }

    val itemButtonClickEventEdit: MutableLiveData<ResponseBody> by lazy {
        MutableLiveData<ResponseBody>()
    }

    val lst = MutableLiveData<List<Product>>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()

    fun getAll(token: String) {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = productRepository.getAll(token)) {
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
                }
            }
        }
    }

    fun setCompleteFalse() {
        complete.value = false
    }

    fun save(product: Product, token: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = productRepository.save(token, product)) {
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

    fun delete(product: Product, token: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = productRepository.delete(token, product)) {
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

    fun onItemButtonClick(product: Product) {
        itemButtonClickEvent.postValue(product)
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