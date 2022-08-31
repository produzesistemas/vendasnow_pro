package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.ApiInterface
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.RetrofitInstance
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepositoryProduct {
    fun getAll(token: String) = flow {
        emit(State.loading())
        var response = get(token)
        if (response.code == 200) { emit(State.success(response.products)) }
        if (response.code == 401) { emit(State.failed(401)) }
    }.flowOn(Dispatchers.IO)

    suspend fun get(token: String) : ResponseBody {
        var responseBody = ResponseBody()
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            retIn.getAllProduct(token).enqueue(object : Callback<List<Product>> {
                override fun onFailure(call: Call<List<Product>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (response.code() == 200) {
                        responseBody.code = 200
                        response.body()?.let{
                            responseBody.products = it
                            continuation.resume(responseBody)
                        }
                    }
                    if (response.code() == 401) {
                        responseBody.code = 401
                        continuation.resume(responseBody)
                    }
                }
            })
        }
    }

    fun save(product: Product, token: String) = flow {
        var responseBody = ResponseBody()
        emit(State.loading())
        var response = insertUpdate(product, token)
        if (response.code == 200) { emit(State.success(response)) }
        if (response.code == 401) { emit(State.failed(401)) }

        // Emit the list to the stream
        emit(State.success(responseBody))
    }.flowOn(Dispatchers.IO)

    private suspend fun insertUpdate(product: Product, token: String) : ResponseBody {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            var responseBody = ResponseBody()
            retIn.saveProduct(token, product).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {

                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() == 200) {
                        responseBody.code = 200
                        continuation.resume(responseBody)
                    }
                    if (response.code() == 401) {
                        responseBody.code = 401
                        continuation.resume(responseBody)
                    }

                }
            })
        }
    }

    fun delete(product: Product, token: String) = flow {
        // Emit loading state
        var responseBody = ResponseBody()
        emit(State.loading())
        var response = deleteProduct(product, token)
        if (response.code == 200) { emit(State.success(response)) }
        if (response.code == 401) { emit(State.failed(401)) }
        emit(State.success(responseBody))
    }.flowOn(Dispatchers.IO)

    private suspend fun deleteProduct(product: Product, token: String) : ResponseBody {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            var responseBody = ResponseBody()
            retIn.deleteProduct(token, product).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {

                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() == 200) {
                        responseBody.code = 200
                        continuation.resume(responseBody)
                    }
                    if (response.code() == 401) {
                        responseBody.code = 401
                        continuation.resume(responseBody)
                    }

                }
            })
        }
    }
}