package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService

class ProductRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getAll(token: String) : NetworkState<List<Product>> {
        val response = retrofitService.getAllProduct(token)
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

    suspend fun delete(token: String, product: Product) : NetworkState<Any> {
        val response = retrofitService.deleteProduct(token, product)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(product)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun save(token: String, product: Product) : NetworkState<Any> {
        val response = retrofitService.saveProduct(token, product)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(product)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
}