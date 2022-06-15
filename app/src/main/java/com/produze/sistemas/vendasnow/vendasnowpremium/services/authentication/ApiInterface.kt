package com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication

import com.produze.sistemas.vendasnow.vendasnowpremium.model.LoginUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.model.UserBody
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type:application/json")
    @POST("account/loginVendasNow")
    fun login(@Body loginUser: LoginUser): retrofit2.Call<Token>

    @Headers("Content-Type:application/json")
    @POST("account/registerVendasNow")
    fun registerUser(
        @Body info: UserBody
    ): retrofit2.Call<ResponseBody>
}

//interface ServiceClient {
//    @GET("client/getAll")
//    fun getAllClient(@Header("Authorization") token: String): retrofit2.Call<List<Client>>
//}

//class ApiClient {
//    private lateinit var apiService: ServiceClient
//    private val BASE_URL: String = "https://produzesistemas.com.br/api/"
//    fun getApiService(): ServiceClient {
//
//        // Initialize ApiService if not initialized yet
//        if (!::apiService.isInitialized) {
//            val retrofit = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//            apiService = retrofit.create(ServiceClient::class.java)
//        }
//
//        return apiService
//    }
//
//}

class RetrofitInstance {
    companion object {
        private const val BASE_URL: String = "https://produzesistemas.com.br/api/"
//        private const val BASE_URL: String = "https://192.168.1.3:44324/api/"

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