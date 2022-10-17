package com.produze.sistemas.vendasnow.vendasnowpremium.model

import java.util.*

data class Subscription (
    var id: Int = 0,
    var planId: Int = 0,
    var subscriptionDate: Date? = null,
    var value: Double = 0.00,
    var plan: Plan?
){

}