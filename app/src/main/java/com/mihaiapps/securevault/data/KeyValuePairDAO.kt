package com.mihaiapps.securevault.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KeyValuePairDAO {
    @Query("SELECT * FROM $KEY_VALUE_TABLE_NAME")
    fun getAll():List<KeyValuePairEntity>

    @Query("SELECT * FROM $KEY_VALUE_TABLE_NAME WHERE ID=:id")
    fun findById(id: String) : KeyValuePairEntity?

    @Insert
    fun insertAll(vararg keyValuePairs: KeyValuePairEntity)

    @Delete
    fun delete(keyValuePair: KeyValuePairEntity)

}