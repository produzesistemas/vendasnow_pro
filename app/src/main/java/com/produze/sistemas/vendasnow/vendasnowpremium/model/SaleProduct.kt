package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class SaleProduct (
        var quantity: Double = 0.00,
        var valueSale: Double = 0.00,
        var product: Product?) {
    constructor():this(0.00,0.00, null)
}