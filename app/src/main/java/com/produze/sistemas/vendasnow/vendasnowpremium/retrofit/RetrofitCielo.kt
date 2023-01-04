package com.produze.sistemas.vendasnow.vendasnowpremium.retrofit
import com.google.gson.GsonBuilder
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RetrofitCielo {

    @Headers("Content-Type:application/json")
    @POST("/1/card")
    suspend fun getCardToken(
        @Header("MerchantId") MerchantId: String?,
        @Header("MerchantKey") MerchantKey: String?,
        @Body Card: Card?
    ): Response<ResponseCard>

    companion object {
        private const val BASE_URL: String = "https://apisandbox.cieloecommerce.cielo.com.br"

        var retrofitService: RetrofitCielo? = null
        fun getInstance() : RetrofitCielo {
            if (retrofitService == null) {
                val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

                val gsonConverterFactory = GsonConverterFactory.create(gson)
                val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }

                val client: OkHttpClient = OkHttpClient.Builder().apply {
                    this.addInterceptor(interceptor)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                }.build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(gsonConverterFactory)
                    .build()
                retrofitService = retrofit.create(RetrofitCielo::class.java)
            }
            return retrofitService!!
        }

    }
}