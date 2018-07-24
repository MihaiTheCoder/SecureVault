package com.mihaiapps.securevault.bl.enc.cipher

import com.mihaiapps.securevault.bl.utils.MemoryPool
import java.io.InputStream
import java.io.OutputStream
import java.security.Key
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream

open class BaseCipher(val name: String, val spec: AlgorithmParameterSpec, val key: Key, private val memoryPool: MemoryPool) {
    var provider: String? = null

    fun getInputStream(inputStream: InputStream): InputStream {
        return SymmetricCipherInputStream(inputStream, getDecrypt(), memoryPool)
    }

    fun getOutputStream(outputStream: OutputStream) : OutputStream {
        return CipherOutputStream(outputStream, getEncrypt())
    }

    private fun getEncrypt(): Cipher {
        val cipher = if (provider != null) Cipher.getInstance(name, provider) else Cipher.getInstance(name)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        return cipher
    }

    private fun getDecrypt(): Cipher {
        val cipher = if (provider != null) Cipher.getInstance(name, provider) else Cipher.getInstance(name)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher
    }
}