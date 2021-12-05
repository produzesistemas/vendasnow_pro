package com.produze.sistemas.vendasnow.vendasnowpremium.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class Sale(
        var obs: String = "",
        var createBy: String = "",
        var salesDate: Date? = null,
        var formPayment: FormPayment?,
        var client: Client?,
        var saleProducts: List<SaleProduct> = ArrayList(),
        var saleServices: List<SaleService> = ArrayList(),
        @ServerTimestamp var createDate: Timestamp? = null,
        @get:Exclude var id: String = "") {
    constructor():this("","",null,null, null, emptyList(), emptyList())

}