package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryAccountReceivable
import java.util.*

class ViewModelAccountReceivable : ViewModel() {
    private val _totalProducts = MutableLiveData<Double>()
    private val _totalServices = MutableLiveData<Double>()
    private val _totalSale = MutableLiveData<Double>()
    private val _totalSaleByFilter = MutableLiveData<Double>()
    var repository = RepositoryAccountReceivable()

    val itemButtonClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

    val detailsClickEvent: MutableLiveData<Sale> by lazy {
        MutableLiveData<Sale>()
    }

//    fun getAllByMonthAndYear(year: Int, month: Int, email: String) = repository.getAllByMonthAndYear(year, month, email)

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

    fun getTotalByFilter(sales: List<Sale>, year: Int, month: Int) {
        var total: Double = 0.00
        val calStart = Calendar.getInstance()
        val calEnd = Calendar.getInstance()
        var dateStart = GregorianCalendar(year, month - 1, 1).time
        var dateEnd = GregorianCalendar(year, month - 1, 1).time

        calStart.time = dateStart
        calEnd.time = dateEnd

        calEnd[Calendar.DAY_OF_MONTH] = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
        calEnd[Calendar.HOUR] = 23
        calEnd[Calendar.MINUTE] = 59
        calEnd[Calendar.MILLISECOND] = 0

        calStart[Calendar.HOUR] = 0
        calStart[Calendar.MINUTE] = 0
        calStart[Calendar.MILLISECOND] = 0

        dateEnd = calEnd.time
        dateStart = calStart.time
        sales.forEach{
            var accounts = it?.accounts?.filter{ it.status == 1 && (it.dueDate!!.after(dateStart) || it.dueDate!! == dateStart) &&
                    (it.dueDate!!.before(dateEnd) || it.dueDate == dateEnd)}
            accounts.forEach {
                total += (it.value)?.toFloat()!!
            }
        }
        _totalSaleByFilter.value = total
    }

    val totalSaleByFilter: LiveData<Double> = Transformations.map(_totalSaleByFilter) {
        it
    }

}