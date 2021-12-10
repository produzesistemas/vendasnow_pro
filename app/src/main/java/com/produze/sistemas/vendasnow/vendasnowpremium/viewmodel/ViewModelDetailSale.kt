package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService

class ViewModelDetailSale : ViewModel() {
    val selected = MutableLiveData<Sale>()
    private val _totalProducts = MutableLiveData<Double>()
    private val _totalServices = MutableLiveData<Double>()
    private val _totalSale = MutableLiveData<Double>()
    private val _totalProfit = MutableLiveData<Double>()

    fun select(item: Sale) {
        selected.value = item
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

    val totalProfit: LiveData<Double> = Transformations.map(_totalProfit) {
        it
    }

    fun getTotalProfit(lstProduct: MutableList<SaleProduct>) {
        var total: Double = 0.00
        lstProduct.forEach {
            if (it.product?.costValue != null) {
                var t = it.valueSale - it.product?.costValue!!
                total=+ t.times(it.quantity)
            }
        }
        _totalProfit.value = total
    }


}