package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.ApiInterface
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.RetrofitInstance
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepositoryClient {
//    var user = FirebaseAuth.getInstance().currentUser
var lst: List<Client?> = ArrayList()
//    fun getAll(email: String) = flow {
//        // Emit loading state
//        emit(State.loading())
//        var lst = FirebaseFirestore.getInstance()
//            .collection("clients")
//            .whereEqualTo("createBy", email)
//            .get().await().documents.map { doc ->
//                    var obj = doc.toObject(Client::class.java)
//                    if (obj != null) {
//                        obj.id = doc.id
//                    }
//                    obj
//                }
//
//        // Emit success state with data
//        emit(State.success(lst))
//    }.catch {
//        // If exception is thrown, emit failed state along with message.
//        emit(State.failed(it.message.toString()))
//    }.flowOn(Dispatchers.IO)

    suspend fun getAll(token: String) : ResponseBody {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            retIn.getAllClient(token).enqueue(object : Callback<List<Client>> {
                override fun onFailure(call: Call<List<Client>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                    if (response.code() == 200) {
                        response.body()?.let{

                            continuation.resume(ResponseBody("", 200, it))
                        }
//                            ?: continuation.resume(it)
                    }
                    if (response.code() == 400) {

                    }
                    if (response.code() == 401) {

                    }
                }
            })

        }
    }

    suspend fun add(client: Client, token: String) : ResponseBody {
        val retIn = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
        return suspendCoroutine { continuation ->
            retIn.saveClient(token, client).enqueue(object : Callback<Client> {
                override fun onFailure(call: Call<Client>, t: Throwable) {

                }

                override fun onResponse(call: Call<Client>, response: Response<Client>) {
                    if (response.code() == 200) {

                        response.body()?.let{
                            continuation.resume(ResponseBody("", 200, arrayListOf()))
                        }
//                            ?: continuation.resume(it)
                    }
                    if (response.code() == 400) {

                    }
                }
            })

        }
    }

//    fun add(client: Client, email: String) = flow<State<DocumentReference>> {
////        client.createBy = email
//        // Emit loading state
//        emit(State.loading())
//        val postRef = FirebaseFirestore.getInstance()
//            .collection("clients").add(client)
//            .addOnSuccessListener { documentReference ->
//
//            }
//            .addOnFailureListener { e ->
//
//            }.await()
//        // Emit success state with post reference
//        emit(State.success(postRef))
//    }.catch {
//        // If exception is thrown, emit failed state along with message.
//        emit(State.failed(it.message.toString()))
//    }.flowOn(Dispatchers.IO)

    fun delete(client: Client) = flow<State<Task<Void>>> {
        // Emit loading state
        emit(State.loading())
        val returnDelete = FirebaseFirestore.getInstance().collection("clients").document(client.id.toString())
                .delete()
                .addOnSuccessListener { Log.d("VendasNowPro", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("VendasNowPro", "Error deleting document", e) }
        // Emit success state with post reference
        emit(State.success(returnDelete))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun update(client: Client) = flow<State<Void?>> {
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
                .collection("clients").document(client.id.toString()).update("name" , client.name, "telephone", client.telephone)
                .addOnSuccessListener { documentReference ->

                }
                .addOnFailureListener { e ->

                }.await()
        // Emit success state with post reference
        emit(State.success(postRef))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)



}