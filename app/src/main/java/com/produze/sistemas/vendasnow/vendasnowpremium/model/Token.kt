package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class Token(
    var token: String = "",
    var email: String = "",
    var userName: String ="",
    var role: String="") {

    override fun toString(): String {
        return token
    }
}