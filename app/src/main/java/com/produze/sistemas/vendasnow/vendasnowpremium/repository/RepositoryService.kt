package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Service
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RepositoryService {
    var user = FirebaseAuth.getInstance().currentUser

    fun getAll() = flow {
        // Emit loading state
        emit(State.loading())
        var lst = FirebaseFirestore.getInstance()
                .collection("services")
                .whereEqualTo("createBy", user?.email.toString())
                .get().await().documents.map { doc ->
                    var obj = doc.toObject(Service::class.java)
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


    fun add(service: Service) = flow<State<DocumentReference>> {
        service.createBy = user?.email.toString()
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
                .collection("services").add(service)
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

    fun delete(service: Service) = flow<State<Task<Void>>> {
        // Emit loading state
        emit(State.loading())
        val returnDelete = FirebaseFirestore.getInstance().collection("services").document(service.id)
                .delete()
                .addOnSuccessListener { Log.d("VendasNowPro", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("VendasNowPro", "Error deleting document", e) }
        // Emit success state with post reference
        emit(State.success(returnDelete))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun update(service: Service) = flow<State<Void?>> {
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
                .collection("services").document(service.id).update("name" , service.name, "value", service.value)
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