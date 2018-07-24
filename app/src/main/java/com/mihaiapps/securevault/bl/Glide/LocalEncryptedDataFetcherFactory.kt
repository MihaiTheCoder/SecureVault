package com.mihaiapps.securevault.bl.Glide

import com.mihaiapps.securevault.bl.LocalFileReader
import com.mihaiapps.securevault.bl.enc.cipher.BaseCipher

class LocalEncryptedDataFetcherFactory(private val fileReader: LocalFileReader,
                                       private val cipher: BaseCipher) {

    fun getDataFetcher(model: String): LocalEncryptedDataFetcher {
        return LocalEncryptedDataFetcher(model, fileReader, cipher)
    }

}