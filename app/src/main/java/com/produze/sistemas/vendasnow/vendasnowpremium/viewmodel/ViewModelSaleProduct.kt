package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct

class ViewModelSaleProduct : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<SaleProduct> by lazy {
        MutableLiveData<SaleProduct>()
    }

    fun onItemButtonClick(saleProduct: SaleProduct) {
        itemButtonClickEvent.postValue(saleProduct)
    }

}