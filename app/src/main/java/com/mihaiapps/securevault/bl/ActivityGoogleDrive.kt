package com.mihaiapps.securevault.bl

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource

class ActivityGoogleDrive(private val context: Context, private val extendableFragment: ExtendableFragment) : ActivityResultDelegate {

    init {
        extendableFragment.setOnActivityResultListener(this)
    }

    /**
     * Handles high-level drive functions like sync
     */
    private var driveClient: DriveClient? = null


    /**
     * Handle access to Drive resources/files.
     */
    private var driveResourceClient: DriveResourceClient? = null

    /**
     * Tracks completion of the drive picker
     */
    private var mOpenItemTaskSource: TaskCompletionSource<DriveId>? = null

    private var driveClientTaskSource = TaskCompletionSource<DriveClient>()

    private var driveResourceClientTaskSource = TaskCompletionSource<DriveResourceClient>()

    private var signInProcessInProgress = false


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == ACTIVITY_RESULT_CODES.REQUEST_CODE_SIGN_IN){
            if(resultCode != Activity.RESULT_OK) {
                onSignInError()
                return
            }
            val getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(getAccountTask.isSuccessful) {
                initializeDriveClient(getAccountTask.result)
            } else {
                onSignInError()
            }
            signInProcessInProgress = false
        }
        else if(requestCode == ACTIVITY_RESULT_CODES.REQUEST_CODE_OPEN_ITEM) {
            if(resultCode != Activity.RESULT_OK) {
                mOpenItemTaskSource?.setException(RuntimeException("Unable to load file"))
                return
            }
            val driveId: DriveId = data!!.getParcelableExtra(
                    OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID)
            mOpenItemTaskSource?.setResult(driveId)
        }
    }

    private fun signIn() {
        if(signInProcessInProgress)
            return
        signInProcessInProgress = true
        val requiredScopes = HashSet<Scope>(2)
        requiredScopes.add(Drive.SCOPE_FILE)
        requiredScopes.add(Drive.SCOPE_APPFOLDER)
        val signInAccount = GoogleSignIn.getLastSignedInAccount(context)

        if(signInAccount?.grantedScopes?.containsAll(requiredScopes) == true) {
            initializeDriveClient(signInAccount)
        } else {
            val signInOptions =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build()
            val googleSignInClient =GoogleSignIn.getClient(context, signInOptions)

            extendableFragment.startActivityForResult(googleSignInClient.signInIntent, ACTIVITY_RESULT_CODES.REQUEST_CODE_SIGN_IN)
        }
    }

    private fun initializeDriveClient(result: GoogleSignInAccount) {
        driveClient = Drive.getDriveClient(context,result)
        driveClientTaskSource.setResult(driveClient)

        driveResourceClient = Drive.getDriveResourceClient(context, result)
        driveResourceClientTaskSource.setResult(driveResourceClient)
    }

    private fun onSignInError() {
        driveClientTaskSource.trySetException(RuntimeException("sign-in failed"))
        driveResourceClientTaskSource.trySetException(RuntimeException("sign-in failed"))
    }

    fun getDriveClient(): Task<DriveClient> {
        signIn()
        return driveClientTaskSource.task
    }

    fun getDriveResourceClient(): Task<DriveResourceClient> {
        signIn()
        return driveResourceClientTaskSource.task
    }

    fun getRootDriveFolder():Task<DriveFolder?> {
        return getDriveResourceClient().onSuccessTask {it ->
            it?.rootFolder!!
        }
    }





    companion object {
        const val TAG = "ActivityGoogleDrive"
    }
}
