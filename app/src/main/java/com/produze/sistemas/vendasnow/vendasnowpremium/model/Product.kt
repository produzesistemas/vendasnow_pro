package com.produze.sistemas.vendasnow.vendasnowpremium.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Product (
        var name: String = "",
        var createBy: String = "",
        var value: Double = 0.00,
        var costValue: Double = 0.00,
        @ServerTimestamp var createDate: Timestamp? = null,
        @get:Exclude var id: String = "") {
    constructor():this("","",0.00, 0.00)
    override fun toString(): String {
        return name
    }
}