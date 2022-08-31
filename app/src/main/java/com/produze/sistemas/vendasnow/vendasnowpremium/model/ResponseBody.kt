package com.produze.sistemas.vendasnow.vendasnowpremium.model

data class ResponseBody(
    var code: Int = 0,
    var clients: List<Client> = ArrayList(),
    var products: List<Product> = ArrayList(),
    var sales: List<Sale> = ArrayList(),
    var accounts: List<Account> = ArrayList()
)