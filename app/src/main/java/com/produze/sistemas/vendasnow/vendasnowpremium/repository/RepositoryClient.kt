package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.ApiInterface
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.RetrofitInstance
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.internal.resumeCancellableWith
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RepositoryClient {

    fun getAll(token: String) = flow {
        emit(State.loading())
        var response = get(token)
        if (response.code == 200) { emit(State.success(response.clients)) }
        if (response.code == 401) { emit(State.failed(401)) }
    }.flowOn(Dispatchers.IO)

    suspend fun get(token: String) : ResponseBody {
        var responseBody = ResponseBody()
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            retIn.getAllClient(token).enqueue(object : Callback<List<Client>> {
                override fun onFailure(call: Call<List<Client>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                    if (response.code() == 200) {
                        responseBody.code = 200
                        response.body()?.let{
                            responseBody.clients = it
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

fun save(client: Client, token: String) = flow {
        var responseBody = ResponseBody()
        emit(State.loading())
    var response = insertUpdate(client, token)
    if (response.code == 200) { emit(State.success(response)) }
    if (response.code == 401) { emit(State.failed(401)) }

        // Emit the list to the stream
        emit(State.success(responseBody))
    }.flowOn(Dispatchers.IO)

    private suspend fun insertUpdate(client: Client, token: String) : ResponseBody {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
        var responseBody = ResponseBody()
            retIn.saveClient(token, client).enqueue(object : Callback<Void> {
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

    fun delete(client: Client, token: String) = flow {
        // Emit loading state
        var responseBody = ResponseBody()
        emit(State.loading())
        var response = deleteClient(client, token)
        if (response.code == 200) { emit(State.success(response)) }
        if (response.code == 401) { emit(State.failed(401)) }
        emit(State.success(responseBody))
    }.flowOn(Dispatchers.IO)



    private suspend fun deleteClient(client: Client, token: String) : ResponseBody {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            var responseBody = ResponseBody()
            retIn.deleteClient(token, client).enqueue(object : Callback<Void> {
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