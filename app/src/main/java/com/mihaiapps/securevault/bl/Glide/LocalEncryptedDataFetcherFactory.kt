package com.mihaiapps.securevault.bl.glide

import com.mihaiapps.securevault.bl.LocalFileReader
import com.mihaiapps.securevault.bl.enc.KeyInitializer
import org.koin.KoinContext
import org.koin.standalone.StandAloneContext

class LocalEncryptedDataFetcherFactory {

    val fileReader: LocalFileReader by inject()
    val keyInitializer: KeyInitializer by inject()

    fun getDataFetcher(model: String): LocalEncryptedDataFetcher {
        return LocalEncryptedDataFetcher(model, fileReader, keyInitializer.cipher)
    }
    inline fun <reified T> inject(name: String = "")
            = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }
}