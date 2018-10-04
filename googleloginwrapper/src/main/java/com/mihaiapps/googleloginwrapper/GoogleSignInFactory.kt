package com.mihaiapps.googleloginwrapper

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task

class GoogleSignInFactory(googleSignInUsecases: Set<GoogleSignInUsecase>) {

    private val requiredScopes: HashSet<Scope> = HashSet()

    private val googleSignInOptionsBuilder: GoogleSignInOptions.Builder

    private var googleSignInWrapper: GoogleSignInWrapper? = null

    init {
        for (useCase in googleSignInUsecases) {
            requiredScopes.addAll(useCase.getRequiredScopes())
        }

        var signInOptionsBuilder = getGoogleSignInOptionsBuilder()
        for (useCase in googleSignInUsecases) {
            signInOptionsBuilder = useCase.addRequestToBuilder(signInOptionsBuilder)
        }

        googleSignInOptionsBuilder = signInOptionsBuilder
    }

    fun get(context: Context, extendableFragment: ExtendableFragment, signInCode: Int): Task<GoogleSignInAccount> {
        var wrapper = googleSignInWrapper
        if (wrapper != null)
            return wrapper.signIn()
        wrapper = GoogleSignInWrapper(context, extendableFragment, signInCode, requiredScopes, googleSignInOptionsBuilder)
        googleSignInWrapper = wrapper
        return wrapper.signIn()
    }

    fun getGoogleSignInOptionsBuilder(): GoogleSignInOptions.Builder {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
    }
}