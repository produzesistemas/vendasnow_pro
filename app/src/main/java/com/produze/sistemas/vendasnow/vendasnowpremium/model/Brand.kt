package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class Brand (var description: String = "") {
    constructor():this("")

    override fun toString(): String {
        return description
    }
}