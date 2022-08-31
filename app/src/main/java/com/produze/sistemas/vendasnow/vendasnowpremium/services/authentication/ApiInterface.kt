package com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication

import com.google.android.gms.tasks.Task
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type:application/json")
    @POST("account/loginVendasNow")
    fun login(@Body loginUser: LoginUser): retrofit2.Call<Token>

    @Headers("Content-Type:application/json")
    @POST("account/registerVendasNow")
    fun registerUser(@Body loginUser: LoginUser): retrofit2.Call<String>

    @Headers("Content-Type:application/json")
    @POST("account/recoverPasswordVendasNow")
    fun forgotPassword(@Body loginUser: LoginUser): retrofit2.Call<String>

    @Headers("Content-Type:application/json")
    @GET("client/getAll")
    fun getAllClient(@Header("Authorization") token: String): retrofit2.Call<List<Client>>

    @POST("client/save")
    fun saveClient(@Header("Authorization") token: String, @Body client: Client): Call<Void>

    @POST("client/delete")
    fun deleteClient(@Header("Authorization") token: String, @Body client: Client): Call<Void>

    @Headers("Content-Type:application/json")
    @GET("product/getAll")
    fun getAllProduct(@Header("Authorization") token: String): retrofit2.Call<List<Product>>

    @POST("product/save")
    fun saveProduct(@Header("Authorization") token: String, @Body product: Product): Call<Void>

    @POST("product/delete")
    fun deleteProduct(@Header("Authorization") token: String, @Body product: Product): Call<Void>
}

class RetrofitInstance {
    companion object {
        private const val BASE_URL: String = "https://produzesistemas.com.br/api/"

        private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        private val client: OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()



        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}