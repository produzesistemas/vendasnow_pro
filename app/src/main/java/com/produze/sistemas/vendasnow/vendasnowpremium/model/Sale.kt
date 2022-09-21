package com.produze.sistemas.vendasnow.vendasnowpremium.model

import java.util.*
import kotlin.collections.ArrayList

data class Sale(
        var id: Int = 0,
        var clientId: Int = 0,
        var obs: String = "",
        var createDate: Date? = null,
        var salesDate: Date? = null,
        var formPayment: FormPayment?,
        var client: Client?,
        var saleProducts: List<SaleProduct> = ArrayList(),
        var saleServices: List<SaleService> = ArrayList(),
        var accounts: List<Account> = ArrayList(),
        var formPaymentId: Int){
    constructor():this(0, 0, "",null,null, null, null, emptyList(), emptyList(), emptyList(),0)

}