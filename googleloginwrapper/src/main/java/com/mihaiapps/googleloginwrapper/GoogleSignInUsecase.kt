package com.mihaiapps.googleloginwrapper

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope

interface GoogleSignInUsecase {
    fun getRequiredScopes(): Set<Scope>
    fun addRequestToBuilder(signInOptionsBuilder: GoogleSignInOptions.Builder): GoogleSignInOptions.Builder

}