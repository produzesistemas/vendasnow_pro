package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.SaleAccountRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import kotlinx.coroutines.*

class AccountReceivableViewModel constructor() : ViewModel() {
    private val _totalSale = MutableLiveData<Double>()
    private val _totalSaleByFilter = MutableLiveData<Double>()
    private val retrofitService = RetrofitService.getInstance()
    private val saleAccountRepository = SaleAccountRepository(retrofitService)
    private var responseBody: ResponseBody = ResponseBody()

    private val _errorMessage = MutableLiveData<ResponseBody>()
    val errorMessage: LiveData<ResponseBody>
        get() = _errorMessage
    val lst = MutableLiveData<List<Account>>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()
    val account = MutableLiveData<Account>()

    val itemButtonClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

    val detailsClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

    val totalSale: LiveData<Double> = Transformations.map(_totalSale) {
        it
    }

    fun getTotalByFilter(accounts: List<Account>) {
        var total: Double = 0.00
            accounts.forEach {
                total += (it.value)?.toFloat()!!
            }
        _totalSaleByFilter.value = total
    }

    val totalSaleByFilter: LiveData<Double> = Transformations.map(_totalSaleByFilter) {
        it
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

    fun getAllByMonthAndYear(filter: FilterDefault, token: String) {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = saleAccountRepository.getAllByMonthAndYear(token, filter)) {
                is NetworkState.Success -> {
                    lst.postValue(response.data!!)
                    loading.value = false
                    getTotalByFilter(response.data!!)
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

    fun getById(id: Int, token: String) {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = saleAccountRepository.getById(token, id)) {
                is NetworkState.Success -> {
                    account.postValue(response.data!!)
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

    fun save(account: Account, token: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = saleAccountRepository.save(token, account)) {
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

}