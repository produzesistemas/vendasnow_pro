package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class Plan (
    var description: String = "",
    var Days: Int = 0,
    var value: Double = 0.00,
    var id: Int = 0) {
    override fun toString(): String {
        return description
    }
}