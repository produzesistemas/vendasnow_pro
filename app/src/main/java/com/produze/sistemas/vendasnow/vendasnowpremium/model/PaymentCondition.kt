package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class PaymentCondition (
        var description: String = "",
        var id: Int = 0) {
    constructor():this("",0)

    override fun toString(): String {
        return description
    }
}
