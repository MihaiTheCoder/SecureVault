package com.mihaiapps.securevault.bl.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder
import com.mihaiapps.securevault.bl.enc.EncryptedFileManager
import org.koin.KoinContext
import org.koin.standalone.StandAloneContext
import java.io.InputStream

class EncryptedStreamBitmapDecoder(private val streamBitmapDecoder: StreamBitmapDecoder): ResourceDecoder<InputStream, Bitmap> {

    private val encryptedFileManager: EncryptedFileManager by inject()

    inline fun <reified T> inject(name: String = "")
            = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

    override fun handles(source: InputStream, options: Options): Boolean {
        return streamBitmapDecoder.handles(encryptedFileManager.getDecryptedStream(source), options)
    }

    override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<Bitmap>? {
        return streamBitmapDecoder.decode(encryptedFileManager.getDecryptedStream(source), width, height, options)
    }

}