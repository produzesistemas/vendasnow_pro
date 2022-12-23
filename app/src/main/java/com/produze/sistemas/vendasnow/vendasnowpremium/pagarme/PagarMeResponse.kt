package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Daniel on 12/05/17.
 */
class PagarMeResponse {
    @Expose
    @SerializedName("id")
    var id: Long = 0

    @Expose
    @SerializedName("date_created")
    var date: String? = null

    @Expose
    @SerializedName("ip")
    var ipAddress: String? = null

    @Expose
    @SerializedName("public_key")
    var publicKey: String? = null

    @JvmName("setId1")
    fun setId(id: Long) {
        this.id = id
    }

    @JvmName("getId1")
    fun getId(): Long {
        return id
    }

    @JvmName("getDate1")
    fun getDate(): String? {
        return date
    }

    @JvmName("setDate1")
    fun setDate(date: String?) {
        this.date = date
    }

    @JvmName("getIpAddress1")
    fun getIpAddress(): String? {
        return ipAddress
    }

    @JvmName("setIpAddress1")
    fun setIpAddress(ipAddress: String?) {
        this.ipAddress = ipAddress
    }

    @JvmName("getPublicKey1")
    fun getPublicKey(): String? {
        return publicKey
    }

    @JvmName("setPublicKey1")
    fun setPublicKey(publicKey: String?) {
        this.publicKey = publicKey
    }

}

