package com.produze.sistemas.vendasnow.vendasnowpremium.retrofit
import com.google.gson.GsonBuilder
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RetrofitService {

    @Headers("Content-Type:application/json")
    @POST("client/getPagination")
    suspend fun getPaginationClients(@Header("Authorization") token: String, @Body filter: FilterDefault): Response<List<Client>>

    @POST("client/save")
    suspend fun saveClient(@Header("Authorization") token: String, @Body client: Client): Response<Void>

    @POST("client/delete")
    suspend fun deleteClient(@Header("Authorization") token: String, @Body client: Client): Response<Void>

    @GET("client/getAll")
    suspend fun getAllClient(@Header("Authorization") token: String): Response<List<Client>>

    @POST("product/save")
    suspend fun saveProduct(@Header("Authorization") token: String, @Body product: Product): Response<Void>

    @POST("product/delete")
    suspend fun deleteProduct(@Header("Authorization") token: String, @Body product: Product): Response<Void>

    @GET("product/getAll")
    suspend fun getAllProduct(@Header("Authorization") token: String): Response<List<Product>>

    @Headers("Content-Type:application/json")
    @POST("sale/save")
    suspend fun saveSale(@Header("Authorization") token: String, @Body sale: Sale): Response<Void>

    @POST("sale/delete")
    suspend fun deleteSale(@Header("Authorization") token: String, @Body sale: Sale): Response<Void>

    @GET("sale/getAll")
    suspend fun getAllSale(@Header("Authorization") token: String): Response<List<Sale>>

    @POST("sale/getAllByMonthAndYear")
    suspend fun getAllByMonthAndYear(@Header("Authorization") token: String, @Body filter: FilterDefault): Response<List<Sale>>

    @GET("saleAccount/{id}")
    suspend fun getAccountById(@Header("Authorization") token: String, @Path("id") id:Int): Response<Account>

    @POST("saleAccount/getAllByMonthAndYear")
    suspend fun getAllAccountByMonthAndYear(@Header("Authorization") token: String, @Body filter: FilterDefault): Response<List<Account>>

    @Headers("Content-Type:application/json")
    @POST("saleAccount/save")
    suspend fun saveAccount(@Header("Authorization") token: String, @Body account: Account): Response<Void>

    @GET("saleAccount/getAllToNotification")
    suspend fun getAllToNotification(@Header("Authorization") token: String): Response<List<Account>>

    companion object {
        private const val BASE_URL: String = "https://produzesistemas.com.br/api/"

        var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
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
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}