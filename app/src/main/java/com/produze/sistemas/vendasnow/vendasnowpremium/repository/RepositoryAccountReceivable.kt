package com.produze.sistemas.vendasnow.vendasnowpremium.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp
import java.util.*

class RepositoryAccountReceivable {
//    var user = FirebaseAuth.getInstance().currentUser
    fun getAllByMonthAndYear(year: Int, month: Int, email: String) = flow {
        var entries: ArrayList<Sale> = ArrayList()
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
            .whereEqualTo("createBy", email)
            .whereIn("formPaymentId", listOf(4, 7, 8, 9, 10))
            .get().await().documents.map { doc ->
                var obj = doc.toObject(Sale::class.java)
                if (obj != null) {
                    obj.id = doc.id
                }
                obj
            }

        lst.forEach { s ->
            if (s?.accounts?.filter{ it.dueDate!! >= timeStampStart &&
                        it.dueDate!! <= timeStampEnd && it.status == 1}?.size!! > 0) {
                entries.add(s)
            }
        }

        // Emit success state with data
//        emit(State.success(lst))
        emit(State.success(entries))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        Log.d(
            "VendasNowPro",
            it.message.toString()
        )
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllCurrentDay(calendar: GregorianCalendar, email: String) = flow {
        var entries: ArrayList<Sale> = ArrayList()
//        val calStart = Calendar.getInstance()
//        val calEnd = Calendar.getInstance()
        var dateStart = calendar.time
//        var dateEnd = calendar.time

//        calStart.time = dateStart
//        calEnd.time = dateEnd
//
//        calEnd[Calendar.DAY_OF_MONTH] = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
//        calEnd[Calendar.HOUR] = 23
//        calEnd[Calendar.MINUTE] = 59
//        calEnd[Calendar.MILLISECOND] = 0
//
//        calStart[Calendar.HOUR] = 0
//        calStart[Calendar.MINUTE] = 0
//        calStart[Calendar.MILLISECOND] = 0
//
//        dateEnd = calEnd.time
//        dateStart = calStart.time

        val timeStampStart = Timestamp(dateStart.time)
//        val timeStampEnd = Timestamp(dateEnd.time)

        // Emit loading state
        emit(State.loading())

        var lst = FirebaseFirestore.getInstance()
            .collection("sales")
            .whereEqualTo("createBy", email.toString())
            .whereIn("formPaymentId", listOf(4, 7, 8, 9, 10))
            .get().await().documents.map { doc ->
                var obj = doc.toObject(Sale::class.java)
                if (obj != null) {
                    obj.id = doc.id
                }
                obj
            }

        var lsta: MutableList<List<Account>> = lst.map { it!!.accounts }.toMutableList()
        val flattened: List<Account> = lsta.flatten()
//        lst.forEach { s ->
//            if (s?.accounts?.filter{ it.dueDate!! == timeStampStart }?.size!! > 0) {
//                entries.add(s)
//            }
//        }

        emit(State.success(flattened))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        Log.d(
            "VendasNowPro",
            it.message.toString()
        )
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}