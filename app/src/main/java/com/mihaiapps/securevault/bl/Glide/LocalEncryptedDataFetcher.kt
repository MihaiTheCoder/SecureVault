package com.mihaiapps.securevault.bl.Glide

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }

    override fun cancel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val stream = cipher.getInputStream(fileReader.getInputStrem(model))
        callback.onDataReady(stream)


    }

}
