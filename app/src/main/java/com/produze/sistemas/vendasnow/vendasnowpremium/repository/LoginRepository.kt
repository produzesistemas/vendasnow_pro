package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService

class LoginRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun login(email: String, secret: String) : NetworkState<Token?> {
        val loginUser = LoginUser(email, secret, "VendasNow")
        val response = retrofitService.login(loginUser)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(response.body())
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun register(email: String, secret: String) : NetworkState<String?> {
        val loginUser = LoginUser(email, secret, "VendasNow")
        val response = retrofitService.registerUser(loginUser)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(response.body())
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun forgot(email: String, secret: String) : NetworkState<String?> {
        val loginUser = LoginUser(email, secret, "VendasNow")
        val response = retrofitService.forgotPassword(loginUser)
        return if (response.isSuccessful) {
            if (response.code() == 200) {
                NetworkState.Success(response.body())
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
}