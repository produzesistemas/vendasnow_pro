package com.produze.sistemas.vendasnow.vendasnowpremium.model

class SaleService (
        var quantity: Int = 0,
        var valueSale: Double = 0.00,
        var service: Service?) {
    constructor():this(0,0.00, null)
}