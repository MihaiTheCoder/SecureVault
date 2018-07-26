package com.mihaiapps.securevault.bl.glide

import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.mihaiapps.securevault.bl.LocalFileReader
import com.mihaiapps.securevault.bl.enc.cipher.BaseCipher
import java.io.InputStream

class LocalEncryptedDataFetcher(private val model: String,
                                private val fileReader: LocalFileReader,
                                private val cipher: BaseCipher) : DataFetcher<InputStream> {
    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun cleanup() {

    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }

    override fun cancel() {

    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val stream = cipher.getInputStream(fileReader.getInputStrem(model))
        callback.onDataReady(stream)


    }

}
