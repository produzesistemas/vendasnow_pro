package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositorySale

class ViewModelDetailAccountReceivable : ViewModel() {
    val selectedAccount = MutableLiveData<Sale>()
    val selectedYear = MutableLiveData<Int>()
    val selectedMonth = MutableLiveData<Int>()

    fun selectedAccount(item: Sale) {
        selectedAccount.value = item
    }

    fun selectedYear(year: Int) {
        selectedYear.value = year
    }

    fun selectedMonth(month: Int) {
        selectedMonth.value = month
    }

}