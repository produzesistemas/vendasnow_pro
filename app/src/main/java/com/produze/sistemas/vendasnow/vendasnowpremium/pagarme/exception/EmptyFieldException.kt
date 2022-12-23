package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception

class EmptyFieldException private constructor(message: String) : RuntimeException(message) {
    companion object {
        operator fun get(field: String): EmptyFieldException {
            return EmptyFieldException("Field " + field + "cant be empty!!")
        }
    }
}