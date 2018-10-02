package com.mihaiapps.googledriverestapiwrapper.restapi

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.mihaiapps.googledriverestapiwrapper.BuildConfig
import com.mihaiapps.googleloginwrapper.ExtendableFragment
import com.mihaiapps.googleloginwrapper.GoogleSignInFactory

class RestDriveApiFactory(private val context: Context, extendableFragment: ExtendableFragment, signInCode: Int):
        GoogleSignInFactory<Drive>(context,extendableFragment, requiredDriveScopes,signInCode) {


    override fun initializeClient(signInAccount: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(context, requiredDriveScopes.map { it.toString() })
        credential.selectedAccount = signInAccount.account
        val httpTransport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = AndroidJsonFactory.getDefaultInstance()
        return Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()
    }
    companion object {
        private val requiredDriveScopes = hashSetOf(Scope(DriveScopes.DRIVE))
    }
}

