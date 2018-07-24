package com.mihaiapps.securevault.bl.enc.cipher

import com.mihaiapps.securevault.bl.utils.MemoryPool
import java.security.Key
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESCipher(spec: AlgorithmParameterSpec, key: Key, memoryPool: MemoryPool) : BaseCipher("AES/CTR/NoPadding", spec, key, memoryPool) {
    companion object {
        fun getInstance(memoryPool: MemoryPool) : AESCipher {
            val iv = ByteArray(16)
            val key = ByteArray(16)
            val random = SecureRandom()
            random.nextBytes(iv)
            random.nextBytes(key)
            val spec = IvParameterSpec(iv)
            return AESCipher(spec, SecretKeySpec(key, "AES"), memoryPool)
        }

        fun fromByteArray(keyAndIv: ByteArray, memoryPool: MemoryPool): AESCipher {
            val iv = ByteArray(16)
            val key = ByteArray(16)
            System.arraycopy(iv, 0, keyAndIv, 0, iv.size)
            System.arraycopy(key, 0, keyAndIv, iv.size, key.size)
            val spec = IvParameterSpec(iv)
            val secretKeySpec = SecretKeySpec(key, 0, key.size, "AES")
            return AESCipher(spec, secretKeySpec, memoryPool)
        }
    }

    fun toByteArray(): ByteArray {
        val ivParameterSpec = spec as IvParameterSpec
        return ivParameterSpec.iv + key.encoded
    }
}