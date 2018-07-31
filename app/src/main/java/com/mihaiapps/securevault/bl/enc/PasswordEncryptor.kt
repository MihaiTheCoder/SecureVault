package com.mihaiapps.securevault.bl.enc

import com.mihaiapps.securevault.data.KeyValuePairDAO
import com.mihaiapps.securevault.data.KeyValuePairEntity

class PasswordEncryptor(private val passwordEncryptorUtils: PasswordEncryptorUtils,
                        private val keyValuePairDAO: KeyValuePairDAO,
                        private val asymmetricKeyStoreManager: AsymmetricKeyStoreManager) {

    lateinit var pass: CharArray

    fun initializeKey() {
        val saltAndIV = passwordEncryptorUtils.generateSaltAndIV()
        keyValuePairDAO.insertAll(KeyValuePairEntity("SaltAndIV", saltAndIV))
    }



}