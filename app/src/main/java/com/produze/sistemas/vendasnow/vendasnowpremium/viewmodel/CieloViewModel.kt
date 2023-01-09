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
    val completeValidateCard = MutableLiveData<Boolean>()

    fun validCardNumber(cardNumber: String) {
        completeValidateCard.value = false
        if (isValid(cardNumber)) {
            completeValidateCard.value = true
        }
    }

    private fun isValid(number: String): Boolean {
        var checksum: Int = 0
        for (i in number.length - 1 downTo 0 step 2) {
            checksum += number[i] - '0'
        }
        for (i in number.length - 2 downTo 0 step 2) {
            val n: Int = (number[i] - '0') * 2
            checksum += if (n > 9) n - 9 else n
        }
        return checksum%10 == 0
    }

    fun getCardToken(MerchantId: String, MerchantKey: String, card: Card) {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = cieloRepository.getCardToken(MerchantId, MerchantKey, card)) {
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
                        Log.d("VendasNowPro", response.response.errorBody()!!.string())
                        onError(response.response.errorBody()!!.string(), 400)
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