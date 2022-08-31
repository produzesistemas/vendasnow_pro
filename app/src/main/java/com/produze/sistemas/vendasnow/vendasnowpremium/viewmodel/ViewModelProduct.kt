package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryProduct

class ViewModelProduct : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<Product> by lazy {
        MutableLiveData<Product>()
    }

    val itemButtonClickEventEdit: MutableLiveData<ResponseBody> by lazy {
        MutableLiveData<ResponseBody>()
    }

    var repository = RepositoryProduct()
    fun save(product: Product, token: String) = repository.save(product, token)
    fun getAll(email: String) = repository.getAll(email)
    fun delete(product: Product, token: String) = repository.delete(product, token)

    fun onItemButtonClick(product: Product) {
        itemButtonClickEvent.postValue(product)
    }

    fun onItemButtonClickEdit(responseBody: ResponseBody) {
        itemButtonClickEventEdit.postValue(responseBody)
    }

}