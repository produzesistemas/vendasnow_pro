package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RepositoryProduct {
//    var user = FirebaseAuth.getInstance().currentUser

    fun getAll(email: String) = flow {
        // Emit loading state
        emit(State.loading())
        var lst = FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo("createBy", email)
                .get().await().documents.map { doc ->
                    var obj = doc.toObject(Product::class.java)
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


    fun add(product: Product, email: String) = flow<State<DocumentReference>> {
        product.createBy = email
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
                .collection("products").add(product)
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

    fun delete(product: Product) = flow<State<Task<Void>>> {
        // Emit loading state
        emit(State.loading())
        val returnDelete = FirebaseFirestore.getInstance().collection("products").document(product.id)
                .delete()
                .addOnSuccessListener { Log.d("VendasNowPro", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("VendasNowPro", "Error deleting document", e) }
        // Emit success state with post reference
        emit(State.success(returnDelete))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun update(product: Product) = flow<State<Void?>> {
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
                .collection("products").document(product.id).update("name" ,
                        product.name, "value",
                        product.value, "costValue", product.costValue)
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