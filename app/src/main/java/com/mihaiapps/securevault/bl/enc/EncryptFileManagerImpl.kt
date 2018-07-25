package com.mihaiapps.securevault.bl.enc

import com.mihaiapps.securevault.bl.LocalFileReader
import com.mihaiapps.securevault.bl.enc.cipher.BaseCipher
import com.mihaiapps.securevault.bl.utils.MemoryPool
import java.io.*

class EncryptFileManagerImpl(val cipher: BaseCipher, private val memoryPool: MemoryPool,
                             private val localFileReader: LocalFileReader) : EncryptedFileManager {

    override fun encryptFile(pathToEncrypt: String, decryptedPath: String, deleteInputFile: Boolean) {
        val inputFile = File(pathToEncrypt)
        val file = File(decryptedPath)
        val fileStream = FileInputStream(inputFile)
        fileStream.use {
            file.createNewFile()
            val outStream = FileOutputStream(file)
            outStream.use {
                encryptToOutput(fileStream, outStream)
            }
        }
        if (deleteInputFile) {
            inputFile.delete()
            val images = localFileReader.getImages{ f -> !f.path.endsWith(EncryptUtils.FILE_EXTENSION)}

            for(image in images) {
                File(image).delete()
            }
        }
    }

    override fun encryptToOutput(streamToEncrypt: InputStream, outStream: OutputStream): Long {
        var bytesCopied: Long = 0
        val buffer = memoryPool.getArray(MemoryPool.DEFAULT_SIZE)
        var bytes = streamToEncrypt.read(buffer)
        val outputStream = cipher.getOutputStream(outStream)
        while (bytes >= 0) {
            outputStream.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = streamToEncrypt.read(buffer)
        }
        memoryPool.putArray(buffer)
        return bytesCopied
    }

    override fun getDecryptedStream(encryptedStream: InputStream): InputStream {
        return cipher.getInputStream(encryptedStream)
    }

}