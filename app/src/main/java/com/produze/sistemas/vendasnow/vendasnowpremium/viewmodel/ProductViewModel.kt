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

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val retrofitService = RetrofitService.getInstance()
    private val productRepository = ProductRepository(retrofitService)

    val itemButtonClickEvent: MutableLiveData<Product> by lazy {
        MutableLiveData<Product>()
    }

    val itemButtonClickEventEdit: MutableLiveData<ResponseBody> by lazy {
        MutableLiveData<ResponseBody>()
    }

    val lst = MutableLiveData<List<Product>>()
    var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
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
                        //movieList.postValue(NetworkState.Error())
                    } else {
                        loading.value = false
                        //movieList.postValue(NetworkState.Error)
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
                        //movieList.postValue(NetworkState.Error())
                    } else {
                        loading.value = false
                        //movieList.postValue(NetworkState.Error)
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
                        _errorMessage.value = "Acesso Negado!"
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        _errorMessage.value = "Falha na tentativa!"
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

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}