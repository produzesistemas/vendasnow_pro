package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService

class SaleRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getAll(token: String) : NetworkState<List<Sale>> {
        val response = retrofitService.getAllSale(token)
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun getAllByMonthAndYear(token: String, filter: FilterDefault) : NetworkState<List<Sale>> {
        val response = retrofitService.getAllByMonthAndYear(token, filter)
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }



    suspend fun delete(token: String, sale: Sale) : NetworkState<Any> {
        val response = retrofitService.deleteSale(token, sale)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(sale)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun save(token: String, sale: Sale) : NetworkState<Any> {
        val response = retrofitService.saveSale(token, sale)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(sale)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
}