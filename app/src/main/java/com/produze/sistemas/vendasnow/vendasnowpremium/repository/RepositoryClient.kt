package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RepositoryClient {
//    var user = FirebaseAuth.getInstance().currentUser

    fun getAll(email: String) = flow {
        // Emit loading state
        emit(State.loading())
        var lst = FirebaseFirestore.getInstance()
            .collection("clients")
            .whereEqualTo("createBy", email)
            .get().await().documents.map { doc ->
                    var obj = doc.toObject(Client::class.java)
                    if (obj != null) {
                        obj.id = doc.id
                    }
                    obj
                }

        // Emit success state with data
        emit(State.success(lst))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    fun add(client: Client, email: String) = flow<State<DocumentReference>> {
        client.createBy = email
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
            .collection("clients").add(client)
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

    fun delete(client: Client) = flow<State<Task<Void>>> {
        // Emit loading state
        emit(State.loading())
        val returnDelete = FirebaseFirestore.getInstance().collection("clients").document(client.id)
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
                .collection("clients").document(client.id).update("name" , client.name, "telephone", client.telephone)
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