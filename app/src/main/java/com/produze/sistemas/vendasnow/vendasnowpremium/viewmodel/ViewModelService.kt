package com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Service
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryService

class ViewModelService : ViewModel() {

    val itemButtonClickEvent: MutableLiveData<Service> by lazy {
        MutableLiveData<Service>()
    }

    val itemButtonClickEventEdit: MutableLiveData<Service> by lazy {
        MutableLiveData<Service>()
    }

    var repository = RepositoryService()
    fun getAll() = repository.getAll()
    fun add(service: Service) = repository.add(service)
    fun update(service: Service) = repository.update(service)
    fun delete(service: Service) = repository.delete(service)

    fun onItemButtonClick(service: Service) {
        itemButtonClickEvent.postValue(service)
    }

    fun onItemButtonClickEdit(service: Service) {
        itemButtonClickEventEdit.postValue(service)
    }

}