package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme

import java.util.regex.Pattern


/**
 * Created by Gunna on 08/03/2018.
 */
object CardUtils {
    private const val REGEX_BRAND_VISA = "4\\d{12}(\\d{3})?"
    private const val REGEX_BRAND_MASTER = "(5[1-5]\\d{4}|677189)\\d{10}"
    private const val REGEX_BRAND_DINERS = "3(0[0-5]|[68]\\d)\\d{11}"
    private const val REGEX_BRAND_DISCOVER = "6(?:011|5[0-9]{2})[0-9]{12}"
    private const val REGEX_BRAND_ELO =
        "((((636368)|(438935)|(504175)|(451416)|(636297))\\d{0,10})|((5067)|(4576)|(4011))\\d{0,12})"
    private const val REGEX_BRAND_AMEX = "3[47]\\d{13}"
    private const val REGEX_BRAND_JCB = "(?:2131|1800|35\\d{3})\\d{11}"
    private const val REGEX_BRAND_AURA = "(5078\\d{2})(\\d{2})(\\d{11})"
    private const val REGEX_BRAND_HIPERCARD = "(606282\\d{10}(\\d{3})?)|(3841\\d{15})"
    private const val REGEX_BRAND_MAESTRO = "(?:5[0678]\\d\\d|6304|6390|67\\d\\d)\\d{8,15}"
    fun getCreditCardBrand(cardNumber: String): String {
        var brand = "undefined"
        if (verifyRegex(cardNumber, REGEX_BRAND_VISA)) {
            brand = "visa"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_MASTER)) {
            brand = "master"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_DINERS)) {
            brand = "diners"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_DISCOVER)) {
            brand = "discover"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_ELO)) {
            brand = "elo"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_AMEX)) {
            brand = "amex"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_JCB)) {
            brand = "jcb"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_AURA)) {
            brand = "aura"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_HIPERCARD)) {
            brand = "hipercard"
        }
        if (verifyRegex(cardNumber, REGEX_BRAND_MAESTRO)) {
            brand = "maestro"
        }
        return brand
    }

    private fun verifyRegex(cardNumber: String, regex: String): Boolean {
        val p = Pattern.compile(regex)
        val m = p.matcher(cardNumber)
        return m.matches()
    }

    fun isValidCardNumber(ccNumber: String): Boolean {
        return try {
            var sum = 0
            var alternate = false
            for (i in ccNumber.length - 1 downTo 0) {
                var n = ccNumber.substring(i, i + 1).toInt()
                if (alternate) {
                    n *= 2
                    if (n > 9) {
                        n = n % 10 + 1
                    }
                }
                sum += n
                alternate = !alternate
            }
            sum % 10 == 0
        } catch (ex: Exception) {
            false
        }
    }
}