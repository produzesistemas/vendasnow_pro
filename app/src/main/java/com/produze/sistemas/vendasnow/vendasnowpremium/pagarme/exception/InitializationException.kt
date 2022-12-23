package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception

class InitializationException private constructor(message: String) : RuntimeException(message) {
    companion object {
        fun get(): InitializationException {
            return InitializationException(
                "You must initialize calling" +
                        " PagarMeAndroid.initialize(apiKey) !!!"
            )
        }
    }
}