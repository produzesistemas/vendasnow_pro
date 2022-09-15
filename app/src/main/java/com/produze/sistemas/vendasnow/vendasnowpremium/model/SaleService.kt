package com.produze.sistemas.vendasnow.vendasnowpremium.model

class SaleService (
    var description: String = "",
    var saleId: Int,
        var quantity: Double = 0.00,
        var valueSale: Double = 0.00) {
    constructor():this("", 0,0.00, 0.00)
}