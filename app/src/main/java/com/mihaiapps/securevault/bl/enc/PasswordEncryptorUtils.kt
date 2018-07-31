package com.mihaiapps.securevault.bl.enc

import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

//https://nelenkov.blogspot.com/2012/04/using-password-based-encryption-on.html
class PasswordEncryptorUtils {
    companion object {
        const val iterationCount = 10000
        private const val keyLength = 256
        const val saltLength = keyLength / 8
    }

    fun generateSaltAndIV(): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secureRandom = SecureRandom()
        val salt = ByteArray(saltLength)
        val iv = ByteArray(cipher.blockSize)
        secureRandom.nextBytes(iv)
        secureRandom.nextBytes(salt)
        return salt + iv
    }

    private fun deriveKeyPbkdf2(salt: ByteArray, pass: CharArray): SecretKey {
        val keySpec = PBEKeySpec(pass, salt, iterationCount, keyLength)
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes = keyFactory.generateSecret(keySpec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun getCipher(pass: CharArray, saltAndIv: ByteArray, cipherMode: Int): Cipher {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val salt = Arrays.copyOfRange(saltAndIv,0,saltLength)
        val iv = Arrays.copyOfRange(saltAndIv,saltLength, saltAndIv.size)

        val key = deriveKeyPbkdf2(salt, pass)

        cipher.init(cipherMode, key, IvParameterSpec(iv))
        return cipher
    }


    fun encrypt(pass: CharArray, saltAndIv: ByteArray, plainBytes: ByteArray): ByteArray {
        return getCipher(pass,saltAndIv, Cipher.ENCRYPT_MODE).doFinal(plainBytes)
    }

    fun decrypt(pass: CharArray, saltAndIv: ByteArray, cipherBytes: ByteArray): ByteArray {
        return getCipher(pass,saltAndIv, Cipher.ENCRYPT_MODE).doFinal(cipherBytes)
    }
}