package com.mihaiapps.securevault.bl.enc

import com.mihaiapps.securevault.bl.enc.cipher.AESCipher
import com.mihaiapps.securevault.bl.utils.MemoryPool
import com.mihaiapps.securevault.data.KeyValuePairDAO
import com.mihaiapps.securevault.data.KeyValuePairEntity

class KeyInitializer(private val keyValuePairDAO: KeyValuePairDAO,
                     private val asymmetricKeyStoreManager: AsymmetricKeyStoreManager,
                     private val memoryPool: MemoryPool) {
    val cipher:AESCipher by lazy {init()}

    fun init(): AESCipher {
        val symmetricKey = keyValuePairDAO.findById(EncryptUtils.SYMMETRIC_KEY_ALIAS)

        return if(symmetricKey == null) {
            val localCipher = AESCipher.getInstance(memoryPool)

            val encryptedKey = asymmetricKeyStoreManager.encryptKey(localCipher.toByteArray())
            keyValuePairDAO.insertAll(KeyValuePairEntity(EncryptUtils.SYMMETRIC_KEY_ALIAS, encryptedKey))
            localCipher
        }
        else {
            val decryptedKey = asymmetricKeyStoreManager.decryptKey(symmetricKey.value)
            AESCipher.fromByteArray(decryptedKey, memoryPool)
        }
    }
}