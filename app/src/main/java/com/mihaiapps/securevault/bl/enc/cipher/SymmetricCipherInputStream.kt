package com.mihaiapps.securevault.bl.enc.cipher

import com.mihaiapps.securevault.bl.utils.MemoryPool
import java.io.FilterInputStream
import java.io.InputStream
import javax.crypto.Cipher

class SymmetricCipherInputStream(`in`: InputStream?, private val cipher: Cipher, private val memoryPool: MemoryPool)
    : FilterInputStream(`in`) {

    companion object {
        private const val BUFFER_SIZE = MemoryPool.DEFAULT_SIZE
    }

    override fun read(buffer: ByteArray?, offset: Int, count: Int): Int {
        val read = `in`.read(buffer, offset, count)

        if(read == -1)
            return -1

        val updateBuffer = memoryPool.getArray(BUFFER_SIZE)
        val remainder = read % BUFFER_SIZE
        val times  = read / BUFFER_SIZE


        var currentReadOffset = offset
        var offsetVar = offset

        for (nBytesProcessed in 0 until times) {
            val bytesProcessed = cipher.update(buffer, offsetVar, BUFFER_SIZE, updateBuffer)
            System.arraycopy(updateBuffer, 0, buffer, currentReadOffset, bytesProcessed)
            currentReadOffset += bytesProcessed
            offsetVar += BUFFER_SIZE
        }

        if(remainder > 0) {
            val bytesProcessed = cipher.update(buffer, offsetVar, remainder, updateBuffer)
            System.arraycopy(updateBuffer, 0, buffer, currentReadOffset, bytesProcessed)
            currentReadOffset += bytesProcessed
        }

        memoryPool.putArray(updateBuffer)
        return currentReadOffset - offset
    }
}