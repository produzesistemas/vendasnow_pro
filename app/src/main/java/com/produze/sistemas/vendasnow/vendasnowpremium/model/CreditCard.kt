package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class CreditCard (
    var cardNumber: String = "",
    var expirationDate: String = "",
    var securityCode: String = "",
    var cardholderName: String = ""
        )