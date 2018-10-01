package com.mihaiapps.securevault


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.mihaiapps.googledriverestapiwrapper.ActivityResultDelegate
import com.mihaiapps.googledriverestapiwrapper.ExtendableFragment
import com.mihaiapps.googledriverestapiwrapper.restapi.RestDriveApiLowLevel
import com.mihaiapps.googledriverestapiwrapper.restapi.RestDriveApiLowLevelFactory
import com.mihaiapps.securevault.bl.ACTIVITY_RESULT_CODES.Companion.REQUEST_CODE_SIGN_IN
import com.mihaiapps.securevault.bl.google_drive_android_api.ActivityGoogleDrive
import com.mihaiapps.securevault.bl.google_drive_android_api.GoogleDriveHighLevelAPI
import com.mihaiapps.securevault.bl.enc.PasswordManager
import kotlinx.android.synthetic.main.fragment_is_password_forgettable.*
import org.koin.android.ext.android.inject


class IsPasswordForgettable : Fragment(), ExtendableFragment {

    private lateinit var activityGoogleDrive: ActivityGoogleDrive
    private lateinit var googleDriveHighLevelAPI: GoogleDriveHighLevelAPI

    private val listeners = ArrayList<ActivityResultDelegate>()
    override fun setOnActivityResultListener(activityResultDelegate: ActivityResultDelegate) {
        listeners.add(activityResultDelegate)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for(listener in listeners)
            listener.onActivityResult(requestCode, resultCode, data)
    }

    open override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }

    val passwordManager: PasswordManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activityGoogleDrive= ActivityGoogleDrive(MainApplication.getContext(), this)
        googleDriveHighLevelAPI = GoogleDriveHighLevelAPI(activityGoogleDrive)
        doSomething()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_is_password_forgettable, container, false)
    }

    fun doSomething() {
        val restApiFactory = RestDriveApiLowLevelFactory(context!!, this, REQUEST_CODE_SIGN_IN)
        val restApiLowLevelTask = restApiFactory.get()
        restApiLowLevelTask.onSuccessTask { restDriveApiLowLevel: RestDriveApiLowLevel? ->
            try {
                AsyncTask.execute {
                    val values = restDriveApiLowLevel!!.getRootDirectory()
                    val file = restDriveApiLowLevel.getFileMetadata(null, "passForgot.txt")
                    if (file != null) {
                        restDriveApiLowLevel.shareFileWithEmails(file, "mihai.petrutiu@qubiz.com")
                    }
                    val x = restDriveApiLowLevel.downloadByName(null, "passForgot.txt")
                }

            } catch (e: Exception) {
                Log.e("x", e.toString())
            }
            Tasks.forResult(2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forget_btn.setOnClickListener{
            passwordManager.createPasswordBackupFile(googleDriveHighLevelAPI) {
                navigateToLogin()
            }
        }


        not_forget_btn.setOnClickListener {
            passwordManager.setIsPasswordForgettable(false)
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_isPasswordForgetable_to_login)
    }


}
