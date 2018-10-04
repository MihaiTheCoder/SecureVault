package com.mihaiapps.securevault.di

import com.mihaiapps.googledriverestapiwrapper.restapi.DriveUseCase
import com.mihaiapps.googleloginwrapper.GoogleSignInFactory
import org.koin.dsl.module.applicationContext

val googleSignInModule = applicationContext {
    bean {DriveUseCase()}
    bean { GoogleSignInFactory(setOf(get<DriveUseCase>())) }
}