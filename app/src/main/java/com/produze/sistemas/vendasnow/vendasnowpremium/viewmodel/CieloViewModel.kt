package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel
import android.util.Log
import androidx.lifecycle.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.CieloRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.SaleRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitCielo
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import kotlinx.coroutines.*

class CieloViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<ResponseBody>()
    val errorMessage: LiveData<ResponseBody>
        get() = _errorMessage

//    private val _totalProducts = MutableLiveData<Double>()
//    private val _totalServices = MutableLiveData<Double>()
//    private val _totalSale = MutableLiveData<Double>()
//    private val _totalSaleByFilter = MutableLiveData<Double>()

    private val retrofitService = RetrofitCielo.getInstance()
    private val cieloRepository = CieloRepository(retrofitService)
    private var responseBody: ResponseBody = ResponseBody()

//    val itemButtonClickEvent: MutableLiveData<Sale> by lazy {
//        MutableLiveData<Sale>()
//    }
//
//    val itemButtonClickEventEdit: MutableLiveData<ResponseBody> by lazy {
//        MutableLiveData<ResponseBody>()
//    }

    val responseCard = MutableLiveData<ResponseCard>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()

    fun getCardToken(MerchantId: String, MerchantKey: String, contentType: String, card: Card) {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = cieloRepository.getCardToken(MerchantId, MerchantKey, contentType, card)) {
                is NetworkState.Success -> {
                    responseCard.postValue(response.data!!)
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