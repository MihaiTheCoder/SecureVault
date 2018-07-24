package com.mihaiapps.securevault.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [(KeyValuePairEntity::class)], version = AppDatabase.VERSION)
abstract class AppDatabase: RoomDatabase() {
    abstract fun keyValuePairDao(): KeyValuePairDAO

    companion object {
        const val VERSION = 1
        const val NAME = "appDatabase"
    }
}