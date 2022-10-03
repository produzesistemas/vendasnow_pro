package com.produze.sistemas.vendasnow.vendasnowpremium.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Account
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.repository.SaleAccountRepository
import com.produze.sistemas.vendasnow.vendasnowpremium.retrofit.RetrofitService
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
    private val retrofitService = RetrofitService.getInstance()
    private val saleAccountRepository = SaleAccountRepository(retrofitService)
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token

    override suspend fun doWork(): Result {
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token != "") {
            withContext(Dispatchers.IO) {
                when (val response = saleAccountRepository.getAllToNotification(token.token)) {
                    is NetworkState.Success -> {
                        createNotification(response.data!!)
                    }
                    is NetworkState.Error -> {

                    }
                }
            }
        }
        return Result.success()
    }

    private fun createNotification(accounts: List<Account>) {
        accounts.forEach{
            NotificationHelper(context, it).createNotification()
        }
    }
}