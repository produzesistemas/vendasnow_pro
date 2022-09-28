package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale

class ViewModelDetailAccountReceivable : ViewModel() {
    val selectedAccount = MutableLiveData<Account>()
    val selectedYear = MutableLiveData<Int>()
    val selectedMonth = MutableLiveData<Int>()

    fun selectedAccount(item: Account) {
        selectedAccount.value = item
    }

    fun selectedYear(year: Int) {
        selectedYear.value = year
    }

    fun selectedMonth(month: Int) {
        selectedMonth.value = month
    }

}