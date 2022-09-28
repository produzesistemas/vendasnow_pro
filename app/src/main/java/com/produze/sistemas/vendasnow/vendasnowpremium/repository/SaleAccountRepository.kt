package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.FilterDefault
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService

class SaleAccountRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getAllByMonthAndYear(token: String, filter: FilterDefault) : NetworkState<List<Account>> {
        val response = retrofitService.getAllAccountByMonthAndYear(token, filter)
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

    suspend fun save(token: String, account: Account) : NetworkState<Any> {
        val response = retrofitService.saveAccount(token, account)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(account)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
}