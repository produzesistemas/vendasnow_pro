package com.produze.sistemas.vendasnow.vendasnowpremium.model

import java.util.*

data class Token(
    var token: String = "",
    var email: String = "",
    var userName: String ="",
    var role: String="",
    var subscriptionDate: Date? = null,) {
    override fun toString(): String {
        return token
    }
}