package com.produze.sistemas.vendasnow.vendasnowpremium.model

class Card (
    var CardNumber: String? = null,
    var Holder: String? = null,
    var ExpirationDate: String? = null,
    var SecurityCode: String? = null,
    var Brand: String? = null,
    var CustomerName: String? = null
        ){
    constructor():this(null,null, null, null, null, null)
}