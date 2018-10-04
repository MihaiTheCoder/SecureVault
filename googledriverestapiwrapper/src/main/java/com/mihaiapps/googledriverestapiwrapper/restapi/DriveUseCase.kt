package com.mihaiapps.googledriverestapiwrapper.restapi

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.mihaiapps.googleloginwrapper.GoogleSignInUsecase

class DriveUseCase: GoogleSignInUsecase {
    override fun getRequiredScopes(): Set<Scope> {
        return requiredDriveScopes
    }

    override fun addRequestToBuilder(signInOptionsBuilder: GoogleSignInOptions.Builder): GoogleSignInOptions.Builder {
        return signInOptionsBuilder
    }

    companion object {
        val requiredDriveScopes = hashSetOf(Scope(DriveScopes.DRIVE))
    }
}