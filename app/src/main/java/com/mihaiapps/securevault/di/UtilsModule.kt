package com.mihaiapps.securevault.di

import com.mihaiapps.securevault.MainApplication
import com.mihaiapps.securevault.bl.LocalFileReader
import org.koin.dsl.module.applicationContext

val utilsModule = applicationContext {
    bean { LocalFileReader(MainApplication.getContext()) }
}