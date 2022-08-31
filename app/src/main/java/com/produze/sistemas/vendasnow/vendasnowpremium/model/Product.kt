package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class Product (
        var name: String = "",
        var value: Double = 0.00,
        var costValue: Double = 0.00,
        var id: Int = 0) {
    constructor():this("",0.00,0.00, 0)
    override fun toString(): String {
        return name
    }
}