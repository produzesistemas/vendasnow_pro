package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


internal class PagarMeService private constructor() {
    private var mGson: Gson? = null
    private var mOkHttpClient: OkHttpClient? = null
    private var mLogInterceptor: HttpLoggingInterceptor? = null
    private var mResponseInterceptor: Interceptor? = null
    private var mServiceInstance: PagarMeApi? = null

    init {
        initAppHostingService()
    }

    private fun getmServiceInstance(): PagarMeApi? {
        return mServiceInstance
    }

    fun getServiceInstance(): PagarMeApi? {
        if (sInstance == null) {
            sInstance = PagarMeService()
        }
        return sInstance!!.getmServiceInstance()
    }

    private fun initAppHostingService() {
        if (mServiceInstance == null) {
            initInterceptors()
            initOkHttp()
            initGson()
            initRetrofitService()
        }
    }

    private fun initGson() {
        mGson = GsonBuilder()
            .setLenient()
            .create()
    }

    private fun initInterceptors() {
        mLogInterceptor = HttpLoggingInterceptor()
        mLogInterceptor!!.level = HttpLoggingInterceptor.Level.BODY
        mResponseInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            Log.w("Retrofit@Response", response.body()!!.string())
            response
        }
    }

    private fun initOkHttp() {
        mOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(mLogInterceptor)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    private fun initRetrofitService() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.PagarMe.API_URL)
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .client(mOkHttpClient)
            .build()
        mServiceInstance = retrofit.create(PagarMeApi::class.java)
    }

    companion object {
        private const val META_DATA_KEY = "me.pagar.EncryptionKey"
        private var sInstance: PagarMeService? = null
        val serviceInstance: PagarMeApi?
            get() {
                if (sInstance == null) {
                    sInstance = PagarMeService()
                }
                return sInstance!!.getmServiceInstance()
            }
    }
}