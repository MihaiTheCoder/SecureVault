package com.mihaiapps.securevault

import android.app.Application
import android.content.Context
import android.util.Log
import com.mihaiapps.securevault.di.modules
import org.koin.android.ext.android.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        sApplication = this
        startKoin(this, modules)
    }

    companion object {
        private var sApplication: Application? = null

        fun getContext(): Context {
            return sApplication!!.applicationContext
        }
    }

}

