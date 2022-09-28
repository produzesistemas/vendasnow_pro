package com.produze.sistemas.vendasnow.vendasnowpremium.model

import java.util.*
import kotlin.collections.ArrayList

data class Sale(
        var id: Int = 0,
        var clientId: Int = 0,
        var saleDate: Date? = null,
        var paymentCondition: PaymentCondition?,
        var client: Client?,
        var saleProduct: List<SaleProduct> = ArrayList(),
        var saleService: List<SaleService> = ArrayList(),
        var account: List<Account> = ArrayList(),
        var paymentConditionId: Int
        ){
    constructor():this(0, 0, null,null,null, emptyList(), emptyList(), emptyList(),0)

}