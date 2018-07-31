package com.mihaiapps.securevault.bl.enc

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class HmacSha1Signature {
    val hmacSHA1 = Mac.getInstance(HMAC_SHA1_ALGORITHM)
    companion object {
        const val HMAC_SHA1_ALGORITHM = "HmacSHA1"
    }

    fun init(key: ByteArray) {
        hmacSHA1.init(SecretKeySpec(key, HMAC_SHA1_ALGORITHM))
    }

    fun hmac(message: ByteArray): ByteArray? {
        return hmacSHA1.doFinal(message)
    }
}