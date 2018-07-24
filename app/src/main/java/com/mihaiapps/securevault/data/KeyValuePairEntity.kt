package com.mihaiapps.securevault.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

const val KEY_VALUE_TABLE_NAME = "KeyValuePair"
@Entity(tableName = KEY_VALUE_TABLE_NAME)
data class KeyValuePairEntity(
        @PrimaryKey
    val ID: String,
    val value: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyValuePairEntity

        if (ID != other.ID) return false
        if (!Arrays.equals(value, other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + Arrays.hashCode(value)
        return result
    }

}