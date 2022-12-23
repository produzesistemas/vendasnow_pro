package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme

class PagarMeRequest {
    private var holderName: String? = null
    private var number: String? = null
    private var expirationDate: String? = null
    private var cvv: String? = null
    private var telephone: String? = null
    private var ddd: String? = null
    private var brand: String? = null

    @JvmName("getHolderName1")
    fun getHolderName(): String? {
        return holderName
    }

    @JvmName("setHolderName1")
    fun setHolderName(holderName: String?) {
        this.holderName = holderName
    }

    @JvmName("getNumber1")
    fun getNumber(): String? {
        return number
    }

    @JvmName("setNumber1")
    fun setNumber(number: String?) {
        this.number = number
    }

    @JvmName("getExpirationDate1")
    fun getExpirationDate(): String? {
        return expirationDate
    }

    @JvmName("setExpirationDate1")
    fun setExpirationDate(expirationDate: String?) {
        this.expirationDate = expirationDate
    }

    @JvmName("getCvv1")
    fun getCvv(): String? {
        return cvv
    }

    @JvmName("setCvv1")
    fun setCvv(cvv: String?) {
        this.cvv = cvv
    }

    @JvmName("getTelephone1")
    fun getTelephone(): String? {
        return telephone
    }

    @JvmName("setTelephone1")
    fun setTelephone(telephone: String?) {
        this.telephone = telephone
    }

    @JvmName("getDdd1")
    fun getDdd(): String? {
        return ddd
    }

    @JvmName("setDdd1")
    fun setDdd(ddd: String?) {
        this.ddd = ddd
    }

    @JvmName("getBrand1")
    fun getBrand(): String? {
        return brand
    }

    @JvmName("setBrand1")
    fun setBrand(brand: String?) {
        this.brand = brand
    }
}