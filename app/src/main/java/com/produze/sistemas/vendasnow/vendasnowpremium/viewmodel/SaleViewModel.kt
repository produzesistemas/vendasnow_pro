package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel
import android.util.Log
import androidx.lifecycle.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.ProductRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.SaleRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import kotlinx.coroutines.*
import java.util.logging.Filter

class SaleViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<ResponseBody>()
    val errorMessage: LiveData<ResponseBody>
        get() = _errorMessage

    private val _totalProducts = MutableLiveData<Double>()
    private val _totalServices = MutableLiveData<Double>()
    private val _totalSale = MutableLiveData<Double>()
    private val _totalSaleByFilter = MutableLiveData<Double>()

    private val retrofitService = RetrofitService.getInstance()
    private val saleRepository = SaleRepository(retrofitService)
    private var responseBody: ResponseBody = ResponseBody()

//    val itemButtonClickEvent: MutableLiveData<Sale> by lazy {
//        MutableLiveData<Sale>()
//    }
//
//    val itemButtonClickEventEdit: MutableLiveData<ResponseBody> by lazy {
//        MutableLiveData<ResponseBody>()
//    }

    val lst = MutableLiveData<List<Sale>>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()
    val complete = MutableLiveData<Boolean>()

    fun getAllByMonthAndYear(filter: FilterDefault, token: String) {
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = saleRepository.getAllByMonthAndYear(token, filter)) {
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

    fun save(sale: Sale, token: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = saleRepository.save(token, sale)) {
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

    fun delete(sale: Sale, token: String) {
        loading.value = true
        viewModelScope.launch {
            when (val response = saleRepository.delete(token, sale)) {
                is NetworkState.Success -> {
                    complete.value = true
                    loading.value = false
//                    itemButtonClickEvent.postValue(client)
                }
                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                        loading.value = false
                        responseBody.code = 401
                        responseBody.message = "Acesso Negado!"
                        _errorMessage.postValue(responseBody)
                    }

                    if (response.response.code() == 400) {
                        loading.value = false
                        responseBody.code = 401
                        responseBody.message = "Falha na tentativa!"
                        _errorMessage.postValue(responseBody)
                    }
                }
            }
        }
    }

//    fun onItemButtonClick(sale: Sale) {
//        itemButtonClickEvent.postValue(sale)
//    }
//
//    fun onItemButtonClickEdit(client: ResponseBody) {
//        itemButtonClickEventEdit.postValue(client)
//    }

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

    val itemButtonClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

    val detailsClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

    fun onItemButtonClick(sale: Sale) {
        itemButtonClickEvent.postValue(sale)
    }

    fun onButtonDetailsClick(sale: Sale) {
        detailsClickEvent.postValue(sale)
    }

    val totalProducts: LiveData<Double> = Transformations.map(_totalProducts) {
        it
    }

    fun getTotalProducts(lst: MutableList<SaleProduct>) {
        var total: Double = 0.00
        lst.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        _totalProducts.value = total
    }

    val totalServices: LiveData<Double> = Transformations.map(_totalServices) {
        it
    }

    fun getTotalServices(lst: MutableList<SaleService>) {
        var total: Double = 0.00
        lst.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        _totalServices.value = total
    }

    val totalSale: LiveData<Double> = Transformations.map(_totalSale) {
        it
    }

    fun getTotalSale(lstService: MutableList<SaleService>, lstProduct: MutableList<SaleProduct>) {
        var total: Double = 0.00
        lstService.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        lstProduct.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        _totalSale.value = total
    }

    fun getTotalSaleToAccount(lstService: MutableList<SaleService>, lstProduct: MutableList<SaleProduct>) : Double{
        var total: Double = 0.00
        lstService.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        lstProduct.forEach {
            total = total.plus((it.valueSale * it.quantity))
        }
        return total
    }

    fun getTotalByFilter(sales: List<Sale>) {
        var total: Double = 0.00
        sales.forEach{
            it.saleProducts.forEach {
                total += (it.valueSale.times(it.quantity))?.toFloat()!!
            }

            it.saleServices.forEach {
                total += (it.valueSale.times(it.quantity))?.toFloat()!!
            }
        }
        _totalSaleByFilter.value = total
    }

    val totalSaleByFilter: LiveData<Double> = Transformations.map(_totalSaleByFilter) {
        it
    }

}