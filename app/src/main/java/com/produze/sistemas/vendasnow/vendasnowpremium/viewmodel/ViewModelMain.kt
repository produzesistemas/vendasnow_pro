package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelMain : ViewModel() {

    val title: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun updateActionBarTitle(_title: String) {
        title.postValue(_title)
    }
}