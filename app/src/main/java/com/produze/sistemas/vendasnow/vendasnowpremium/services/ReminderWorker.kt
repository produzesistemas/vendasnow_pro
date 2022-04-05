package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.util.*

class ReminderWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
    var repository = RepositoryAccountReceivable()
    private var calendar: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
    private lateinit var auth: FirebaseAuth
    override suspend fun doWork(): Result {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        withContext(Dispatchers.IO) {

            if (user != null) {
                var dateStart = calendar.time
                val timeStampStart = Timestamp(dateStart.time)
                var lst = FirebaseFirestore.getInstance()
                    .collection("sales")
                    .whereEqualTo("createBy", user.email.toString())
                    .whereIn("formPaymentId", listOf(4, 7, 8, 9, 10))
                    .get().await().documents.map { doc ->
                        var obj = doc.toObject(Sale::class.java)
                        if (obj != null) {
                            obj.id = doc.id
                        }
                        obj
                    }

                lst.forEach { s ->
                    if (s?.accounts?.filter{
                            val calendarAccount = Calendar.getInstance()
                            calendarAccount.time = it.dueDate
                            calendarAccount.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                            calendarAccount.get(Calendar.MONTH) + 1 == calendar.get(Calendar.MONTH) &&
                            calendarAccount.get(Calendar.DAY_OF_MONTH) ==  calendar.get(Calendar.DAY_OF_MONTH)

                    }?.size!! > 0) {
                        s.client?.let {
                            NotificationHelper(context).createNotification(
                                "VendasNow Pro",
                                it.name
                            )
                        }
                    }
                }

//                lst.forEach { s ->
//                    s?.accounts?.filter { it.dueDate!! == timeStampStart }?.forEach {
//                        if (s != null) {
//                            s.client?.let { it1 ->
//                                NotificationHelper(context).createNotification(
//                                    "VendasNow Pro",
//                                    it1.name
//                                )
//                            }
//                        }
//                    }
//                }
                }
        }
        return Result.success()
    }
}