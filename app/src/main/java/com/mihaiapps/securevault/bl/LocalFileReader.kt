package com.mihaiapps.securevault.bl

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import android.provider.OpenableColumns
import android.provider.MediaStore
import android.content.ContentUris
import android.provider.DocumentsContract
import android.content.ContentResolver

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

    fun getAppsImageDirectory(unencryptedGalleryUri: Uri): String? {
        val filName = getFileName(unencryptedGalleryUri)
        return File(getPicturesDirectory(), filName).absolutePath
    }

    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)

        val columnIndex = cursor!!.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }

}