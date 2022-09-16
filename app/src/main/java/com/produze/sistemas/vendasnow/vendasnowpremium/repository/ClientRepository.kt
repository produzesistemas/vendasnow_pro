package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService

class ClientRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getPagination(token: String, filter: FilterDefault) : NetworkState<List<Client>> {
        val response = retrofitService.getPaginationClients(token, filter)
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

    suspend fun getAll(token: String) : NetworkState<List<Client>> {
        val response = retrofitService.getAllClient(token)
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

    suspend fun delete(token: String, client: Client) : NetworkState<Void> {
        val response = retrofitService.deleteClient(token, client)
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

    suspend fun save(token: String, client: Client) : NetworkState<Any> {
        val response = retrofitService.saveClient(token, client)
        return if (response.isSuccessful) {

            if (response.code() == 200) {
                val responseBody = response.body()

                NetworkState.Success(client)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
}