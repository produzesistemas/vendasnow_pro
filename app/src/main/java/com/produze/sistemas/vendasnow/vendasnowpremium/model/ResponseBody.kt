package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class ResponseBody(
    var code: Int = 0,
    var clients: List<Client> = ArrayList()
)