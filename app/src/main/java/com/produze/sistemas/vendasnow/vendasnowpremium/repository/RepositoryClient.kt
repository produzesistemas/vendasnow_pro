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

    suspend fun getAll(token: String) : List<Client> {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            retIn.getAllClient(token).enqueue(object : Callback<List<Client>> {
                override fun onFailure(call: Call<List<Client>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                    if (response.code() == 200) {
                        response.body()?.let{
                            continuation.resume(it)
                        }
                    }
                    if (response.code() == 400) {

                    }
                }
            })
        }
    }

fun insert(client: Client, token: String) = flow {
        var responseBody = ResponseBody()
        emit(State.loading())
        add(client, token)
        // Emit the list to the stream
        emit(State.success(responseBody))
    }.flowOn(Dispatchers.IO) // Use the IO thread for this Flow

    suspend fun add(client: Client, token: String) : ResponseBody {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
        var responseBody = ResponseBody()
            retIn.saveClient(token, client).enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {

                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                            responseBody.code = 200
                            continuation.resume(responseBody)
                        }
                }
            })
        }
    }

//    fun delete(client: Client) = flow<State<Task<Void>>> {
//        // Emit loading state
//        emit(State.loading())
//
//        emit(State.success())
//    }.catch {
//        // If exception is thrown, emit failed state along with message.
//        emit(State.failed(it.message.toString()))
//    }.flowOn(Dispatchers.IO)
//
//    fun update(client: Client) = flow<State<Void?>> {
//        // Emit loading state
//        emit(State.loading())
//        val postRef = FirebaseFirestore.getInstance()
//                .collection("clients").document(client.id.toString()).update("name" , client.name, "telephone", client.telephone)
//                .addOnSuccessListener { documentReference ->
//
//                }
//                .addOnFailureListener { e ->
//
//                }.await()
//        // Emit success state with post reference
//        emit(State.success(postRef))
//    }.catch {
//        // If exception is thrown, emit failed state along with message.
//        emit(State.failed(it.message.toString()))
//    }.flowOn(Dispatchers.IO)



}