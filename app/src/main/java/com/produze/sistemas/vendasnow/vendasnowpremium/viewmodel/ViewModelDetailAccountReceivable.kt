package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale

class ViewModelDetailAccountReceivable : ViewModel() {
    val selectedAccount = MutableLiveData<Sale>()

    fun selectedAccount(item: Sale) {
        selectedAccount.value = item
    }




}