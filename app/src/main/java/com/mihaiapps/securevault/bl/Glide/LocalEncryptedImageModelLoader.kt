package com.mihaiapps.securevault.bl.Glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.mihaiapps.securevault.bl.LocalFileReader
import com.mihaiapps.securevault.bl.enc.EncryptUtils
import java.io.InputStream



class LocalEncryptedImageModelLoader(private val fetcherFactory: LocalEncryptedDataFetcherFactory): ModelLoader<String, InputStream> {

    override fun buildLoadData(model: String, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), fetcherFactory.getDataFetcher(model))
    }

    override fun handles(model: String): Boolean {
        return model.endsWith(EncryptUtils.FILE_EXTENSION) &&
                model.startsWith(LocalFileReader.PATH_PREFIX)
    }

}