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
        var accounts: List<Account> = ArrayList(),
        var formPaymentId: Int,
        @ServerTimestamp var createDate: Timestamp? = null,
        @get:Exclude var id: String = "",
        @get:Exclude var year: Int = 0, @get:Exclude var month: Int = 0){
    constructor():this("","",null,null, null, emptyList(), emptyList(), emptyList(), 0)

}