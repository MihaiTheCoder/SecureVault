package com.mihaiapps.googledriverestapiwrapper

import android.app.Activity
import android.content.Context
import android.content.Intent

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

abstract class GoogleSignInFactory<T>(private val context: Context,
                                   private val extendableFragment: ExtendableFragment,
                                   private val requiredScopes: Set<Scope>,
                                   private val signInCode: Int) : ActivityResultDelegate {

    private fun onSignInError(error: Exception) {
        signInTaskSource.trySetException(error)
    }

    abstract fun initializeClient(signInAccount: GoogleSignInAccount): T
    private val signInTaskSource = TaskCompletionSource<T>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode != signInCode) {
            signInProcessInProgress = false
            return
        }

        if(resultCode != Activity.RESULT_OK) {
            onSignInError(RuntimeException("RESULT CODE NOT OK-ResultCode:$resultCode"))
            signInProcessInProgress = false
            return
        }

        val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        if(accountTask.isSuccessful)
            signInTaskSource.setResult(initializeClient(accountTask.result))
        else {
            if (accountTask.exception != null)
                onSignInError(accountTask.exception!!)
            else
                onSignInError(RuntimeException("SignIn not successful, it's not me boss"))
        }
        signInProcessInProgress = false
    }


    init {
        extendableFragment.setOnActivityResultListener(this)
    }

    private var signInProcessInProgress = false
    fun signIn(): Task<T> {
        if (signInProcessInProgress)
            return signInTaskSource.task

        signInProcessInProgress = true
        val signInAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (signInAccount?.grantedScopes?.containsAll(requiredScopes) == true && signInAccount.account != null) {
            signInTaskSource.setResult(initializeClient(signInAccount))
        } else {
            val signInOptionsBuilder =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
            for (scope in requiredScopes)
                signInOptionsBuilder.requestScopes(scope)
            val signInOptions = signInOptionsBuilder.build()
            val googleSignIn = GoogleSignIn.getClient(context, signInOptions)!!
            extendableFragment.startActivityForResult(googleSignIn.signInIntent, signInCode)
        }
        return signInTaskSource.task
    }


}