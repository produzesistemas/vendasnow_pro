package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositorySale

class ViewModelDetailAccountReceivable : ViewModel() {
    val selectedAccount = MutableLiveData<Sale>()

    fun selectedAccount(item: Sale, year: Int, month: Int) {
        item.year = year
        item.month = month
        selectedAccount.value = item
    }



}