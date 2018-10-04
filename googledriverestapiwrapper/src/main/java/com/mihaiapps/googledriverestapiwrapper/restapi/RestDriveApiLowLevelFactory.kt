package com.mihaiapps.googledriverestapiwrapper.restapi

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.mihaiapps.googledriverestapiwrapper.BuildConfig
import com.mihaiapps.googleloginwrapper.ExtendableFragment
import com.mihaiapps.googleloginwrapper.GoogleSignInFactory

class RestDriveApiLowLevelFactory(private val context: Context,
                                  private val extendableFragment: ExtendableFragment,
                                  private val signInCode: Int,
                                  private val googleSignInFactory: GoogleSignInFactory) {
    fun get(): Task<RestDriveApiLowLevel> {

        return googleSignInFactory.get(context, extendableFragment, signInCode).onSuccessTask {
            Tasks.forResult(RestDriveApiLowLevel(initializeClient(it!!)))
        }
    }

    private fun initializeClient(signInAccount: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(context, DriveUseCase.requiredDriveScopes.map { it.toString() })
        credential.selectedAccount = signInAccount.account
        val httpTransport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = AndroidJsonFactory.getDefaultInstance()
        return Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()
    }
}

