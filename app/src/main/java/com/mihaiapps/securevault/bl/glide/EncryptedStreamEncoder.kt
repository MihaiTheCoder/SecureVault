package com.mihaiapps.securevault.bl.glide

import android.util.Log
import com.bumptech.glide.load.Encoder
import com.bumptech.glide.load.Options
import com.mihaiapps.securevault.bl.enc.EncryptedFileManager
import org.koin.KoinContext
import org.koin.standalone.StandAloneContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EncryptedStreamEncoder: Encoder<InputStream> {
    private val encryptedFileManager: EncryptedFileManager by inject()

    inline fun <reified T> inject(name: String = "")
            = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

    override fun encode(data: InputStream, file: File, options: Options): Boolean {
        return try {
            encryptedFileManager.encryptToOutput(data, FileOutputStream(file))
            true
        } catch (e: Exception) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Failed to encode Bitmap", e)
            }
            false
        }
    }

    companion object {
        val TAG = "securevault"
    }

}