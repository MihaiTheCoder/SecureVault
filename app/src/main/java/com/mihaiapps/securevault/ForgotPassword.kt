package com.mihaiapps.securevault

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.mihaiapps.googledriverestapiwrapper.ActivityResultDelegate
import com.mihaiapps.googledriverestapiwrapper.ExtendableFragment
import com.mihaiapps.securevault.bl.google_drive_android_api.ActivityGoogleDrive
import com.mihaiapps.securevault.bl.google_drive_android_api.GoogleDriveHighLevelAPI
import com.mihaiapps.securevault.bl.enc.PasswordManager
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import org.koin.android.ext.android.inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ForgotPassword.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ForgotPassword.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ForgotPassword : Fragment(), ExtendableFragment, ForgotPasswordUI {

    private val listeners = ArrayList<ActivityResultDelegate>()
    override fun setOnActivityResultListener(activityResultDelegate: ActivityResultDelegate) {
        listeners.add(activityResultDelegate)
    }

    private lateinit var activityGoogleDrive: ActivityGoogleDrive
    private lateinit var googleDriveHighLevelAPI: GoogleDriveHighLevelAPI
    val passwordManager: PasswordManager by inject()


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for(listener in listeners)
            listener.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activityGoogleDrive= ActivityGoogleDrive(MainApplication.getContext(), this)
        googleDriveHighLevelAPI = GoogleDriveHighLevelAPI(activityGoogleDrive)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forgot_password_content_txt.showSoftKeyboard()

        forgot_pass_btn.setOnClickListener{
            forgot_password_content_txt.hideSoftKeyboard()
            val expectedContent = forgot_password_content_txt.text.toString()
            passwordManager.onForgotPassword(googleDriveHighLevelAPI, expectedContent, this)
        }
    }

    override fun changePass() {
        val bundle = Bundle()
        bundle.putString(Login.RESET_PASSWORD_ARG_NAME, Login.RESET_PASSWORD_ARG_VALUE)
        NavHostFragment.findNavController(this).navigate(R.id.action_forgotPassword_to_login, bundle)
    }

    override fun reEnterFileContent() {
        forgot_password_content_txt.text.clear()
        forgot_password_content_txt.showSoftKeyboard()
    }
}
