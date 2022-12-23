package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception

class InvalidKeyException private constructor(message: String) : RuntimeException(message) {
    companion object {
        fun get(): InvalidKeyException {
            return InvalidKeyException(
                "You must provide a valid" +
                        " non-empty PagarMe api key !! "
            )
        }
    }
}