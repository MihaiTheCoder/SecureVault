package com.mihaiapps.securevault.bl.enc

import java.io.InputStream
import java.io.OutputStream

interface EncryptedFileManager {
    fun encryptToOutput(streamToEncrypt: InputStream, outStream: OutputStream): Long
    fun getDecryptedStream(encryptedStream: InputStream) : InputStream
    fun encryptFile(pathToEncrypt: String, decryptedPath: String, deleteInputFile: Boolean)
    fun getEncryptedStream(outStream: OutputStream): OutputStream
}