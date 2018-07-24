package com.mihaiapps.securevault.bl

import android.content.Context
import java.io.InputStream

class LocalFileReader(private val context: Context) {
    companion object {
        const val PATH_PREFIX = "/storage"
    }

    fun getInputStrem(path: String): InputStream {
        return context.openFileInput(path)
    }
}