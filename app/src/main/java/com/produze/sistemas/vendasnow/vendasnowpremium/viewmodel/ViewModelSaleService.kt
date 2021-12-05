package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService

class ViewModelSaleService : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<SaleService> by lazy {
        MutableLiveData<SaleService>()
    }

    fun onItemButtonClick(saleService: SaleService) {
        itemButtonClickEvent.postValue(saleService)
    }

}