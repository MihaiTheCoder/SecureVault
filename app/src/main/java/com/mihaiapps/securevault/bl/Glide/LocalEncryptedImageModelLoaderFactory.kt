package com.mihaiapps.securevault.bl.Glide

import android.content.Context
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.mihaiapps.securevault.bl.LocalFileReader
import com.mihaiapps.securevault.bl.enc.cipher.BaseCipher
import java.io.InputStream

class LocalEncryptedImageModelLoaderFactory(val context: Context, private val aesCipher: BaseCipher) : ModelLoaderFactory<String, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, InputStream> {
        return LocalEncryptedImageModelLoader(
                LocalEncryptedDataFetcherFactory(LocalFileReader(context),aesCipher))
    }

    override fun teardown() {
    }

}