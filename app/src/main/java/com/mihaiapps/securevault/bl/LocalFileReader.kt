package com.mihaiapps.securevault.bl

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class LocalFileReader(private val context: Context) {
    companion object {
        const val PATH_PREFIX = "/storage"
        val DIRECTORY_PICTURES = Environment.DIRECTORY_PICTURES
    }

    fun getInputStrem(path: String): InputStream {
        return FileInputStream(File(path))
    }

    fun getPicturesDirectory(): File {
        return context.getExternalFilesDir(DIRECTORY_PICTURES)
    }

    fun getImages(filter: ((File)-> Boolean)? = null): List<String> {
        return if (filter == null) {
            getPicturesDirectory().listFiles().map { it.absolutePath }
        } else {
            getPicturesDirectory().listFiles(filter).map { it.absolutePath }
        }
    }

    fun deleteFiles(files: List<String>) {
        for (file in files) {
            File(file).delete()

        }
    }
}