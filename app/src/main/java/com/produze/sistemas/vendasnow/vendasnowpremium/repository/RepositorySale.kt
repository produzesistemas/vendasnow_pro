package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp
import java.util.*


class RepositorySale {
    var user = FirebaseAuth.getInstance().currentUser

    fun getAll() = flow {
        // Emit loading state
        emit(State.loading())
        var lst = FirebaseFirestore.getInstance()
                .collection("sales")
                .whereEqualTo("createBy", user?.email.toString())
                .get().await().documents.map { doc ->
                    var obj = doc.toObject(Sale::class.java)
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

    fun add(sale: Sale) = flow<State<DocumentReference>> {
        sale.createBy = user?.email.toString()
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
                .collection("sales").add(sale)
                .addOnSuccessListener { doc ->

                }
                .addOnFailureListener { e ->

                }.await()
        // Emit success state with post reference
        emit(State.success(postRef))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun delete(sale: Sale) = flow<State<Task<Void>>> {
        // Emit loading state
        emit(State.loading())
        val returnDelete = FirebaseFirestore.getInstance().collection("sales").document(sale.id)
                .delete()
                .addOnSuccessListener { Log.d(
                        "VendasNowPro",
                        "DocumentSnapshot successfully deleted!"
                ) }
                .addOnFailureListener { e -> Log.w("VendasNowPro", "Error deleting document", e) }
        // Emit success state with post reference
        emit(State.success(returnDelete))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllByMonthAndYear(year: Int, month: Int) = flow {
        val calStart = Calendar.getInstance()
        val calEnd = Calendar.getInstance()
        var dateStart = GregorianCalendar(year, month - 1, 1).time
        var dateEnd = GregorianCalendar(year, month - 1, 1).time

        calStart.time = dateStart
        calEnd.time = dateEnd

        calEnd[Calendar.DAY_OF_MONTH] = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
        calEnd[Calendar.HOUR] = 23
        calEnd[Calendar.MINUTE] = 59
        calEnd[Calendar.MILLISECOND] = 0

        calStart[Calendar.HOUR] = 0
        calStart[Calendar.MINUTE] = 0
        calStart[Calendar.MILLISECOND] = 0

        dateEnd = calEnd.time
        dateStart = calStart.time

        val timeStampStart = Timestamp(dateStart.time)
        val timeStampEnd = Timestamp(dateEnd.time)

        // Emit loading state
        emit(State.loading())

        var lst = FirebaseFirestore.getInstance()
            .collection("sales")
            .whereEqualTo("createBy", user?.email.toString())
            .whereGreaterThanOrEqualTo("salesDate", timeStampStart)
            .whereLessThanOrEqualTo("salesDate", timeStampEnd)
            .get().await().documents.map { doc ->
                var obj = doc.toObject(Sale::class.java)
                if (obj != null) {
                    obj.id = doc.id
                }
                obj
            }

        // Emit success state with data
        emit(State.success(lst))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        Log.d(
                "VendasNowPro",
                it.message.toString()
        )
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllByYear(year: Int) = flow {
        val calStart = Calendar.getInstance()
        val calEnd = Calendar.getInstance()
        var dateStart = GregorianCalendar(year,  0, 1).time
        calEnd.add(Calendar.MONTH, -1);

        var dateEnd = GregorianCalendar(year, 11, calEnd.getMaximum(Calendar.DAY_OF_MONTH)).time

        calEnd[Calendar.HOUR] = 23
        calEnd[Calendar.MINUTE] = 59
        calEnd[Calendar.MILLISECOND] = 0
        calEnd[Calendar.YEAR] = year

        calStart[Calendar.HOUR] = 0
        calStart[Calendar.MINUTE] = 0
        calStart[Calendar.MILLISECOND] = 0

        dateEnd = calEnd.time

        val timeStampStart = Timestamp(dateStart.time)
        val timeStampEnd = Timestamp(dateEnd.time)

        // Emit loading state
        emit(State.loading())

        var lst = FirebaseFirestore.getInstance()
                .collection("sales")
                .whereEqualTo("createBy", user?.email.toString())
                .whereGreaterThanOrEqualTo("salesDate", timeStampStart).whereLessThanOrEqualTo("salesDate", timeStampEnd)
                .get().await().documents.map { doc ->
                    var obj = doc.toObject(Sale::class.java)
                    if (obj != null) {
                        obj.id = doc.id
                    }
                    obj
                }

        // Emit success state with data
        emit(State.success(lst))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        Log.d(
                "VendasNowPro",
                it.message.toString()
        )
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun update(sale: Sale) = flow<State<Void?>> {
        // Emit loading state
        emit(State.loading())
        val postRef = FirebaseFirestore.getInstance()
            .collection("sales").document(sale.id).update("accounts", sale.accounts)
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

    fun getById(id: String) = flow {

        // Emit loading state
        emit(State.loading())

        var lst = FirebaseFirestore.getInstance()
            .collection("sales")
            .document(id)
            .get()
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->

            }.await()

        // Emit success state with data
        emit(State.success(lst))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        Log.d(
            "VendasNowPro",
            it.message.toString()
        )
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

}