package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class SaleProduct (
        var quantity: Int = 0,
        var valueSale: Double = 0.00,
        var product: Product?) {
    constructor():this(0,0.00, null)
}