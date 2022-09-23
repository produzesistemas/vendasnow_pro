package com.produze.sistemas.vendasnow.vendasnowpremium.model

import com.google.firebase.firestore.Exclude

data class PaymentCondition (
        var name: String = "",
        @get:Exclude var id: String = "") {
    constructor():this("","")

    override fun toString(): String {
        return name
    }
}
