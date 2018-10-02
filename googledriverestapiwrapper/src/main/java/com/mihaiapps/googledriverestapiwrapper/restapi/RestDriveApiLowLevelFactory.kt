package com.mihaiapps.googledriverestapiwrapper.restapi

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.mihaiapps.googleloginwrapper.ExtendableFragment

class RestDriveApiLowLevelFactory(private val context: Context,
                                  private val extendableFragment: com.mihaiapps.googleloginwrapper.ExtendableFragment,
                                  private val signInCode: Int) {
    fun get(): Task<RestDriveApiLowLevel> {
        val factory = RestDriveApiFactory(context, extendableFragment, signInCode)
        return factory.signIn().onSuccessTask {
            Tasks.forResult(RestDriveApiLowLevel(it!!))
        }
    }
}