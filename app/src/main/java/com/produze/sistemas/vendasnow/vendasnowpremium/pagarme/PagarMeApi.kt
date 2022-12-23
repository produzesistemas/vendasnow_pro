package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Daniel on 11/05/17.
 */
internal interface PagarMeApi {
    @GET(Constants.PagarMe.CARD_HASH)
    fun getKeyHash(@Query("encryption_key") key: String?): Call<PagarMeResponse?>?
}