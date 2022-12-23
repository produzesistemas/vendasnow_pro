package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception

class InvalidCardNumberException private constructor(message: String) : RuntimeException(message) {
    companion object {
        fun get(): InvalidCardNumberException {
            return InvalidCardNumberException("Invalid card number !!")
        }
    }
}