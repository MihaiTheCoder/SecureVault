package com.mihaiapps.securevault.bl.glide

import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.load.EncodeStrategy
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceEncoder
import com.bumptech.glide.load.engine.Resource
import com.mihaiapps.securevault.bl.enc.EncryptedFileManager
import org.koin.KoinContext
import org.koin.standalone.StandAloneContext
import java.io.File
import java.io.FileOutputStream


class EncryptedBitmapResourceEncoder: ResourceEncoder<Bitmap> {

    private val encryptedFileManager: EncryptedFileManager by inject()

    inline fun <reified T> inject(name: String = "")
            = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

    companion object {
        val COMPRESSION_QUALITY: Option<Int> = Option.memory(
                "com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionQuality", 90)

        val COMPRESSION_FORMAT: Option<Bitmap.CompressFormat> =
                Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionFormat")

        const val TAG = "securevault"
    }

    override fun getEncodeStrategy(options: Options): EncodeStrategy {
        return EncodeStrategy.TRANSFORMED
    }

    override fun encode(data: Resource<Bitmap>, file: File, options: Options): Boolean {
        val bitmap = data.get()
        val format = getFormat(bitmap, options)
        val quality = options.get(COMPRESSION_QUALITY)!!

        return try {
            val os = encryptedFileManager.getEncryptedStream(FileOutputStream(file))
            os.use {
                bitmap.compress(format, quality, it)
            }
            true
        } catch (e: Exception) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Failed to encode Bitmap", e)
            }
            false
        }
    }

    private fun getFormat(bitmap:Bitmap, options:Options): Bitmap.CompressFormat {
        val format: Bitmap.CompressFormat? = options.get(COMPRESSION_FORMAT)

        return when {
            format != null -> format
            bitmap.hasAlpha() -> Bitmap.CompressFormat.PNG
            else -> Bitmap.CompressFormat.JPEG
        }
    }

}