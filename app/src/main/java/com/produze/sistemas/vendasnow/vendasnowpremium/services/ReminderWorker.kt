package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.LoginUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Sale
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.RepositoryAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.SaleAccountRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.ApiInterface
import com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication.RetrofitInstance
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterAccountReceivable
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.util.*

class ReminderWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
    private val retrofitService = RetrofitService.getInstance()
    private val saleAccountRepository = SaleAccountRepository(retrofitService)
    private var calendar: GregorianCalendar = Calendar.getInstance() as GregorianCalendar
//    private lateinit var auth: FirebaseAuth

    private var datasource: DataSourceUser? = null
    private lateinit var token: Token

    override suspend fun doWork(): Result {
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        withContext(Dispatchers.IO) {

            when (val response = saleAccountRepository.getAllToNotification(token.token)) {
                is NetworkState.Success -> {
                    response.data!!.forEach{
                        NotificationHelper(context, it).createNotification()
                    }

                }
                is NetworkState.Error -> {

                }
            }


//            if (token.email != null) {
//                var dateStart = calendar.time
//                val timeStampStart = Timestamp(dateStart.time)
//                var lst = FirebaseFirestore.getInstance()
//                    .collection("sales")
//                    .whereEqualTo("createBy", token.email)
//                    .whereIn("formPaymentId", listOf(4, 7, 8, 9, 10))
//                    .get().await().documents.map { doc ->
//                        var obj = doc.toObject(Sale::class.java)
//                        if (obj != null) {
//                            obj.id = doc.id
//                        }
//                        obj
//                    }
//
//                lst.forEach { s ->
//                    if (s?.accounts?.filter{
//                        val calendarDueDate = Calendar.getInstance()
//                        calendarDueDate.time = it.dueDate
//                        calendarDueDate.time < calendar.time && it.status == 1
//                    }?.size!! > 0) {
//                            NotificationHelper(context, s).createNotification()
//                    }
//                }
//
//                }
        }
        return Result.success()
    }
}