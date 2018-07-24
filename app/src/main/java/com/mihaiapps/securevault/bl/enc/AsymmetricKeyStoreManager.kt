package com.mihaiapps.securevault.bl.enc

interface AsymmetricKeyStoreManager {
    fun masterKeyExists(): Boolean
    fun createMasterKeyIfNotInStore()
    fun deleteMasterKey()
    fun encryptKey(keyToEncrypt: ByteArray): ByteArray
    fun decryptKey(keyToDecrypt: ByteArray): ByteArray
}