package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryProduct

class ViewModelProduct : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<Product> by lazy {
        MutableLiveData<Product>()
    }

    val itemButtonClickEventEdit: MutableLiveData<Product> by lazy {
        MutableLiveData<Product>()
    }

    var repository = RepositoryProduct()
    fun getAll() = repository.getAll()
    fun add(product: Product) = repository.add(product)
    fun update(product: Product) = repository.update(product)
    fun delete(product: Product) = repository.delete(product)

    fun onItemButtonClick(service: Product) {
        itemButtonClickEvent.postValue(service)
    }

    fun onItemButtonClickEdit(service: Product) {
        itemButtonClickEventEdit.postValue(service)
    }

}