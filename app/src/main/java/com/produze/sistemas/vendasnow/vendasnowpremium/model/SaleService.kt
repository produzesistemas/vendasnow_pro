package com.produze.sistemas.vendasnow.vendasnowpremium.model

class SaleService (
        var quantity: Double = 0.00,
        var valueSale: Double = 0.00,
        var service: Service?) {
    constructor():this(0.00,0.00, null)
}