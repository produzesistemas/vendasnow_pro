package com.produze.sistemas.vendasnow.vendasnowpremium.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.util.*

data class Account (
    var status: Int = 0,
    var uniqueIDNotification: Int = 0,
    var mRequestCode: Int = 0,
    var value: Double = 0.00,
    var amountPaid: Double? = null,
    var dueDate: Date? = null,
    var dateOfPayment: Date? = null,
    @get:Exclude var id: String = "") {
    constructor():this(0, 0, 0,0.00,null, null, null)

}