package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Plan
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.ProductRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.SubscriptionRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import kotlinx.coroutines.*
class SubscriptionViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<ResponseBody>()
    val errorMessage: LiveData<ResponseBody>
        get() = _errorMessage

    private val retrofitService = RetrofitService.getInstance()
    private val subscriptionRepository = SubscriptionRepository(retrofitService)
    private var responseBody: ResponseBody = ResponseBody()

    val plans = MutableLiveData<List<Plan>>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()
    val plan = MutableLiveData<Plan>()

    fun getAllPlan() {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = subscriptionRepository.getAllPlan()) {
                is NetworkState.Success -> {
                    plans.postValue(response.data!!)
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

    fun selectPlan(selectedPlan: Plan) {
         plan.value = selectedPlan
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