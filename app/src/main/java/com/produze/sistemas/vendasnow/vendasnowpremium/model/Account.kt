package com.produze.sistemas.vendasnow.vendasnowpremium.model

import java.util.*

data class Account (
    var sale: Sale?,
    var status: Int = 0,
    var uniqueIDNotification: Int = 0,
    var mRequestCode: Int = 0,
    var saleId: Int = 0,
    var value: Double = 0.00,
    var amountPaid: Double? = null,
    var dueDate: Date? = null,
    var dateOfPayment: Date? = null,
    var id: Int) {
    constructor():this( null,0, 0, 0, 0,0.00,null, null, null, 0)

}