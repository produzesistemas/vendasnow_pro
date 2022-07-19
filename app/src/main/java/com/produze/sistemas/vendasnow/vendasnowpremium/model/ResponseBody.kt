package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class ResponseBody(
    var message: String,
    var code: Int,
    var clients: List<Client> = ArrayList()
)