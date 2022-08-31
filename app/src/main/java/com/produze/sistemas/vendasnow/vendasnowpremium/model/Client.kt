package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class Client(
        var name: String = "",
        var telephone: String ="",
        var id: Int = 0) {
    constructor():this("","",0)

    override fun toString(): String {
        return name
    }
}

