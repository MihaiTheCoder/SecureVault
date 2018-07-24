package com.mihaiapps.securevault.bl.utils

import android.util.SparseArray
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MemoryPool {
    private val lock = ReentrantLock()
    private val map = SparseArray<Queue<ByteArray>>()

    fun getArray(size: Int): ByteArray {
        val arrayQueue = getOrAddQueue(size)
        return arrayQueue.poll() ?: ByteArray(size)
    }

    private fun getOrAddQueue(size: Int): Queue<ByteArray> {
        var arrayQueue = map.get(size, null)
        if (arrayQueue == null) {
            lock.withLock {
                arrayQueue = map.get(size, null)
                if (arrayQueue == null) {
                    arrayQueue = LinkedList<ByteArray>()
                    map.append(size, arrayQueue)
                }
            }
        }
        return arrayQueue
    }

    fun putArray(array:ByteArray) {
        getOrAddQueue(array.size).add(array)
    }

    companion object {
        const val DEFAULT_SIZE = DEFAULT_BUFFER_SIZE
    }
}