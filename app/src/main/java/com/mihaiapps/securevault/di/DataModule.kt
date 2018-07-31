package com.mihaiapps.securevault.di

import androidx.room.Room
import com.mihaiapps.securevault.MainApplication
import com.mihaiapps.securevault.data.AppDatabase
import com.mihaiapps.securevault.data.AppDatabaseFactory
import org.koin.dsl.module.applicationContext

val dataModule = applicationContext {
    bean { AppDatabaseFactory() }
    bean { get<AppDatabaseFactory>().get() }

    bean { get<AppDatabase>().keyValuePairDao() }
}