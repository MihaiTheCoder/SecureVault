package com.mihaiapps.securevault


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.drive.CreateFileActivityOptions
import com.mihaiapps.securevault.bl.ActivityGoogleDrive
import com.mihaiapps.securevault.bl.ActivityResultDelegate
import com.mihaiapps.securevault.bl.ExtendableFragment
import com.mihaiapps.securevault.bl.GoogleDriveHighLevelAPI
import com.mihaiapps.securevault.bl.enc.PasswordManager
import kotlinx.android.synthetic.main.fragment_is_password_forgettable.*
import org.koin.android.ext.android.inject
import java.nio.charset.Charset
import java.security.SecureRandom


class IsPasswordForgettable : Fragment(), ExtendableFragment {

    private val listeners = ArrayList<ActivityResultDelegate>()
    override fun setOnActivityResultListener(activityResultDelegate: ActivityResultDelegate) {
        listeners.add(activityResultDelegate)
    }

    private lateinit var activityGoogleDrive: ActivityGoogleDrive
    private lateinit var googleDriveHighLevelAPI: GoogleDriveHighLevelAPI

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for(listener in listeners)
            listener.onActivityResult(requestCode, resultCode, data)
    }

    override open fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }

    val passwordManager: PasswordManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activityGoogleDrive= ActivityGoogleDrive(MainApplication.getContext(),this)
        googleDriveHighLevelAPI = GoogleDriveHighLevelAPI(activityGoogleDrive)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_is_password_forgettable, container, false)
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
