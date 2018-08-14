package com.mihaiapps.securevault.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.mihaiapps.securevault.bl.enc.PasswordManager

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

    @Update(onConflict = REPLACE)
    fun update(keyValuePair: KeyValuePairEntity)

}