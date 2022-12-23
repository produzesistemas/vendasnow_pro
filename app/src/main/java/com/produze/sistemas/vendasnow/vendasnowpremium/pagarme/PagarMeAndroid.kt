package com.produze.sistemas.vendasnow.vendasnowpremium.pagarme

import android.text.TextUtils.isEmpty
import com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.CardUtils.getCreditCardBrand
import com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.CardUtils.isValidCardNumber
import com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception.EmptyFieldException
import com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception.InvalidCardNumberException
import com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception.InvalidKeyException
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.security.KeyFactory
import java.util.*
import javax.crypto.Cipher
import android.util.Base64;
import com.produze.sistemas.vendasnow.vendasnowpremium.pagarme.exception.InitializationException
import java.io.IOException
import java.net.URLEncoder
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class PagarMeAndroid private constructor(key: String) {
    private val mRequest: PagarMeRequest
    private val mApiService: PagarMeApi
    private val mKey: String

    init {
        mRequest = PagarMeRequest()
        mApiService = PagarMeService.serviceInstance!!
        mKey = key
    }

    fun holderName(value: String?): PagarMeAndroid {
        mRequest.setHolderName(value)
        return this
    }

    fun number(value: String?): PagarMeAndroid {
        mRequest.setNumber(value)
        mRequest.setBrand(getCreditCardBrand(value!!))
        return this
    }

    fun expirationDate(value: String?): PagarMeAndroid {
        mRequest.setExpirationDate(value)
        return this
    }

    fun cvv(value: String?): PagarMeAndroid {
        mRequest.setCvv(value)
        return this
    }

    fun telephone(value: String?): PagarMeAndroid {
        mRequest.setTelephone(value)
        return this
    }

    fun ddd(value: String?): PagarMeAndroid {
        mRequest.setDdd(value)
        return this
    }

    fun generateCardHash(listener: PagarMeListener) {
        if (!isEmpty(mKey)) {
            if (checkFieldsRequest(listener)) generateKeyHash(listener)
        } else listener.onError(InvalidKeyException.get())
    }

    private fun checkFieldsRequest(listener: PagarMeListener): Boolean {
        if (isEmpty(mRequest.getNumber())) {
            listener.onError(getEmptyFieldException("Card number"))
            return false
        }
        if (isEmpty(mRequest.getHolderName())) {
            listener.onError(getEmptyFieldException("Holder name"))
            return false
        }
        if (isEmpty(mRequest.getExpirationDate())) {
            listener.onError(getEmptyFieldException("Expiration date"))
            return false
        }
        if (isEmpty(mRequest.getCvv())) {
            listener.onError(getEmptyFieldException("CVV"))
            return false
        }
        if (isEmpty(mRequest.getBrand())) {
            listener.onError(getEmptyFieldException("Brand"))
            return false
        }
        if (!mRequest.getNumber()?.let { isValidCardNumber(it) }!!) {
            listener.onError(InvalidCardNumberException.get())
            return false
        }
        return true
    }

    private fun getEmptyFieldException(field: String): Exception {
        return EmptyFieldException[field]
    }

    private val unexpectedErrorException: Exception
        private get() = Exception(Throwable("Unexpected error pagarme response =  null "))

    private fun generateKeyHash(listener: PagarMeListener) {
        getPublicKey(object : Callback<PagarMeResponse?> {
            override fun onResponse(call: Call<PagarMeResponse?>?, response: Response<PagarMeResponse?>) {
                if (response.code() === 200) {
                    try {
                        if (response.body() != null) {
                            val cardHash = buildCardHash(response.body()!!)
                            if (!isEmpty(cardHash)) listener.onSuccess(
                                mRequest,
                                response.body(),
                                cardHash
                            ) else listener.onError(
                                cardHashError
                            )
                        } else {
                            listener.onError(unexpectedErrorException)
                        }
                    } catch (e: Exception) {
                        listener.onError(e)
                    }
                } else {
                    listener.onError(
                        Exception(
                            Throwable(
                                ("PagarMeService:/Error generating card hash: "
                                        + response.code()) + " " + response.message()
                            )
                        )
                    )
                }
            }

            override fun onFailure(call: Call<PagarMeResponse?>?, t: Throwable?) {
                listener.onError(Exception(t))
            }
        })
    }

    private val cardHashError: Exception
        private get() = RuntimeException("Unknown error generating card hash!!")

    private fun getPublicKey(callbackApi: Callback<PagarMeResponse?>) {
        mApiService.getKeyHash(mKey)!!.enqueue(callbackApi)
    }

    private fun buildCardHash(publicKeyResponse: PagarMeResponse): String {
        try {
            val cipher: Cipher = Cipher.getInstance("RSA/None/PKCS1PADDING")
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val cardHashId = publicKeyResponse.id.toInt()
            var rawPublicKey = publicKeyResponse.publicKey

            //PAGARME PUBLIC KEYS COME IN PEM FORMAT, SO WE NEED TO CONVERT'EM
            //TO DER FORMAT SO THAT THEY CAN PLAY NICE WITH X509EncodedKeySpec.
            rawPublicKey = rawPublicKey
                ?.replace("\\s*-----BEGIN PUBLIC KEY-----\\s*".toRegex(), "")
                ?.replace("\\s*-----END PUBLIC KEY-----\\s*".toRegex(), "")
                ?.replace("\\s".toRegex(), "")
                ?.replace("[\n]".toRegex(), "")
                ?.replace("[\r]".toRegex(), "")
                ?.replace("[\t]".toRegex(), "")
                ?.replace("[ ]".toRegex(), "")
            val decodedPublicKey: ByteArray = Base64.decode(rawPublicKey, 0)

            //SUCCESSFULLY CONVERTED THE PUBLIC KEY TO DER FORMAT.
            val publicKeySpec = X509EncodedKeySpec(decodedPublicKey)
            val publicKey: PublicKey = keyFactory.generatePublic(publicKeySpec)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            //NOW FORMATTING CARD INFORMATION IN THE FORMAT EXPECTED BY PAGARME.
            val cardInformation: String = java.lang.String.format(
                Locale.US, (
                        "card_number=%s&card_holder_name=%s&card_expiration_date=%s"
                                + "&card_cvv=%s"), mRequest.getNumber()!!.replace(" ", "").trim(),
                URLEncoder.encode(mRequest.getHolderName()!!.trim(), "UTF-8").replace("+", "%20"),
                mRequest.getExpirationDate()!!.replace("/", "").trim(), mRequest.getCvv()!!.trim()
            )

            //THEN WE USE OUR CIPHER TO ENCRYPT AND THEN BASE64-ENCODE THE CARD INFORMATION,
            //AND FINALLY, APPEND THE ASSIGNED CARD ID AS PREFIX OF THE RESULT TO COMPOSE THE CARD HASH.
            val encryptedCardInformation: ByteArray =
                cipher.doFinal(cardInformation.toByteArray(charset("UTF-8")))
            return java.lang.String.format(
                Locale.US, "%d_%s", cardHashId,
                (Base64.encodeToString(encryptedCardInformation, Base64.DEFAULT))
            )
                .replace("\\s".toRegex(), "")

            //NOW YOU MIGHT WANT TO SEND THE CARD HASH TO YOUR BACKEND SERVER.
        } catch (invalidKey: InvalidKeySpecException) {
            //PROBLEMS WITH THE PUBLIC KEY RECEIVED FROM PAGARME.
        } catch (invalidKey: InvalidKeyException) {
        } catch (encryptionException: BadPaddingException) {
            //I/O ERRORS HAPPENED WHILE TRYING TO ENCRYPT CARD INFORMATION.
        } catch (encryptionException: IllegalBlockSizeException) {
        } catch (encryptionException: IOException) {
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        return ""
    }

    @Throws(Exception::class)
    private fun encrypt(plain: String, publicKey: String): String {
        val pubKeyPEM = publicKey.replace("-----BEGIN PUBLIC KEY-----\n", "")
            .replace("-----END PUBLIC KEY-----", "")
        val keyBytes: ByteArray =
            Base64.decode(pubKeyPEM.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val key: PublicKey = keyFactory.generatePublic(spec)
        val cipher: Cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes: ByteArray = cipher.doFinal(plain.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    interface PagarMeListener {
        fun onSuccess(cardRequest: PagarMeRequest?, response: PagarMeResponse?, cardHash: String?)
        fun onError(e: Exception?)
    }

    companion object {
        private var sInstance: PagarMeAndroid? = null
        fun initialize(key: String) {
            if (!isEmpty(key)) sInstance = PagarMeAndroid(key) else throw InvalidKeyException.get()
        }

        fun getsInstance(): PagarMeAndroid? {
            if (sInstance == null) throw InitializationException.get()
            return sInstance
        }
    }
}