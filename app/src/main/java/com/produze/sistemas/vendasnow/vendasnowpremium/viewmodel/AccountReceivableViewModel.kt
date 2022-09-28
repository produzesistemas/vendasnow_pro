package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.SaleAccountRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import kotlinx.coroutines.*

class AccountReceivableViewModel constructor() : ViewModel() {
    private val _totalProducts = MutableLiveData<Double>()
    private val _totalServices = MutableLiveData<Double>()
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

    val itemButtonClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

    val detailsClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

//    fun onItemButtonClick(sale: Sale) {
//        itemButtonClickEvent.postValue(sale)
//    }
//
//    fun onButtonDetailsClick(sale: Sale) {
//        detailsClickEvent.postValue(sale)
//    }
//
//    val totalProducts: LiveData<Double> = Transformations.map(_totalProducts) {
//        it
//    }
//
//    fun getTotalProducts(lst: MutableList<SaleProduct>) {
//        var total: Double = 0.00
//        lst.forEach {
//            total = total.plus((it.valueSale * it.quantity))
//        }
//        _totalProducts.value = total
//    }
//
//    val totalServices: LiveData<Double> = Transformations.map(_totalServices) {
//        it
//    }
//
//    fun getTotalServices(lst: MutableList<SaleService>) {
//        var total: Double = 0.00
//        lst.forEach {
//            total = total.plus((it.valueSale * it.quantity))
//        }
//        _totalServices.value = total
//    }

    val totalSale: LiveData<Double> = Transformations.map(_totalSale) {
        it
    }

//    fun getTotalSale(lstService: MutableList<SaleService>, lstProduct: MutableList<SaleProduct>) {
//        var total: Double = 0.00
//        lstService.forEach {
//            total = total.plus((it.valueSale * it.quantity))
//        }
//        lstProduct.forEach {
//            total = total.plus((it.valueSale * it.quantity))
//        }
//        _totalSale.value = total
//    }
//
//    fun getTotalSaleToAccount(lstService: MutableList<SaleService>, lstProduct: MutableList<SaleProduct>) : Double{
//        var total: Double = 0.00
//        lstService.forEach {
//            total = total.plus((it.valueSale * it.quantity))
//        }
//        lstProduct.forEach {
//            total = total.plus((it.valueSale * it.quantity))
//        }
//        return total
//    }

    fun getTotalByFilter(accounts: List<Account>) {
        var total: Double = 0.00
//        val calStart = Calendar.getInstance()
//        val calEnd = Calendar.getInstance()
//        var dateStart = GregorianCalendar(year, month - 1, 1).time
//        var dateEnd = GregorianCalendar(year, month - 1, 1).time
//
//        calStart.time = dateStart
//        calEnd.time = dateEnd
//
//        calEnd[Calendar.DAY_OF_MONTH] = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
//        calEnd[Calendar.HOUR] = 23
//        calEnd[Calendar.MINUTE] = 59
//        calEnd[Calendar.MILLISECOND] = 0
//
//        calStart[Calendar.HOUR] = 0
//        calStart[Calendar.MINUTE] = 0
//        calStart[Calendar.MILLISECOND] = 0
//
//        dateEnd = calEnd.time
//        dateStart = calStart.time
//        sales.forEach{
//            var accounts = it?.account?.filter{ it.status == 1 && (it.dueDate!!.after(dateStart) || it.dueDate!! == dateStart) &&
//                    (it.dueDate!!.before(dateEnd) || it.dueDate == dateEnd)}
            accounts.forEach {
                total += (it.value)?.toFloat()!!
            }
//        }
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