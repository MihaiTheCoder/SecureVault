package com.mihaiapps.securevault.di

import androidx.room.Room
import com.mihaiapps.securevault.MainApplication
import com.mihaiapps.securevault.data.AppDatabase
import org.koin.dsl.module.applicationContext

val dataModule = applicationContext {
    bean { Room.databaseBuilder(MainApplication.getContext(), AppDatabase::class.java, AppDatabase.NAME).allowMainThreadQueries().build() }

    bean { get<AppDatabase>().keyValuePairDao() }
}