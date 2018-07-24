package com.mihaiapps.securevault.bl.enc

import com.mihaiapps.securevault.bl.enc.cipher.AESCipher
import com.mihaiapps.securevault.bl.utils.MemoryPool
import com.mihaiapps.securevault.data.KeyValuePairDAO
import com.mihaiapps.securevault.data.KeyValuePairEntity

class KeyInitializer(private val keyValuePairDAO: KeyValuePairDAO,
                     private val asymmetricKeyStoreManager: AsymmetricKeyStoreManager,
                     private val memoryPool: MemoryPool) {
    lateinit var cipher:AESCipher

    fun init() {
        asymmetricKeyStoreManager.createMasterKeyIfNotInStore()
        val symmetricKey = keyValuePairDAO.findById(EncryptUtils.SYMMETRIC_KEY_ALIAS)

        if(symmetricKey == null) {
            cipher = AESCipher.getInstance(memoryPool)

            val encryptedKey = asymmetricKeyStoreManager.encryptKey(cipher.toByteArray())
            keyValuePairDAO.insertAll(KeyValuePairEntity(EncryptUtils.SYMMETRIC_KEY_ALIAS, encryptedKey))
        }
        else {
            val decryptedKey = asymmetricKeyStoreManager.decryptKey(symmetricKey.value)
            cipher = AESCipher.fromByteArray(decryptedKey, memoryPool)
        }
    }
}