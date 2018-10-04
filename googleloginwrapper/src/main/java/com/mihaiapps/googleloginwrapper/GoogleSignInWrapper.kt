package com.mihaiapps.googleloginwrapper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

class GoogleSignInWrapper(private val context: Context,
                          private val extendableFragment: ExtendableFragment,
                          private val signInCode: Int,
                          private val requiredScopes: Set<Scope>,
                          private val signInOptionsBuilder: GoogleSignInOptions.Builder) : ActivityResultDelegate  {
    private val signInTaskSource = TaskCompletionSource<GoogleSignInAccount>()

    private var signInProcessInProgress = false

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
            signInTaskSource.setResult(accountTask.result)
        else {
            if (accountTask.exception != null)
                onSignInError(accountTask.exception!!)
            else
                onSignInError(RuntimeException("SignIn not successful, it's not me boss"))
        }
        signInProcessInProgress = false
    }

    private fun onSignInError(error: Exception) {
        signInTaskSource.trySetException(error)
    }

    fun signIn(): Task<GoogleSignInAccount> {
        if (signInProcessInProgress)
            return signInTaskSource.task

        signInProcessInProgress = true
        val signInAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (signInAccount?.grantedScopes?.containsAll(requiredScopes) == true && signInAccount.account != null && !signInAccount.isExpired) {
            signInTaskSource.setResult(signInAccount)
        } else {

            val signInOptions = signInOptionsBuilder.build()
            val googleSignIn = GoogleSignIn.getClient(context, signInOptions)!!
            extendableFragment.startActivityForResult(googleSignIn.signInIntent, signInCode)
        }
        return signInTaskSource.task
    }

    init {
        extendableFragment.setOnActivityResultListener(this)
    }

}