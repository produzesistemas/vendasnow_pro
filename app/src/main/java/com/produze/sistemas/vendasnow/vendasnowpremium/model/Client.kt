package com.produze.sistemas.vendasnow.vendasnowpremium.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Client(
        var name: String = "",
//        var createBy: String = "",
        var telephone: String ="",
    var id: Int = 0
//        @ServerTimestamp var createDate: Timestamp? = null,
//        @get:Exclude var id: String = ""
) {
    constructor():this("","",0)

    override fun toString(): String {
        return name
    }
}

