package com.produze.sistemas.vendasnow.vendasnowpremium.model

import com.google.gson.annotations.SerializedName

data class UserBody (
    @SerializedName("token") val token: String,
    @SerializedName("email") var email: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("role") val role: String)