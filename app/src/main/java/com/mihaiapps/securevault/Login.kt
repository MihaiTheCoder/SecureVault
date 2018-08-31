package com.mihaiapps.securevault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.mihaiapps.securevault.bl.enc.PasswordManager
import com.mihaiapps.securevault.data.AppDatabase
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject

class Login : Fragment(){

    private val passwordManager: PasswordManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        change_password_btn.setOnClickListener { changePassword() }

        zeroize_btn.setOnClickListener { zeroize() }
        forgot_password_redirect_btn.setOnClickListener{
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_forgotPassword)
        }

        val shouldResetPassword = arguments?.getString(RESET_PASSWORD_ARG_NAME) == Login.RESET_PASSWORD_ARG_NAME
        if(shouldResetPassword) {
            resetPassword()
            return
        }
        if (passwordManager.getIsPinRegistered()) {
            attemptLogin { loginResult, _ -> onLoginAttempt(loginResult) }
            pin_label.text = getString(R.string.LOGIN_PIN)
        } else {
            if (passwordManager.hasDecidedIfPasswordIsForgettable())
                registerPin()
            else
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_login_to_isPasswordForgetable)
        }
    }

    private fun zeroize() {
        passwordManager.setIsPinRegistered(false)
        passwordManager.deletePasswordIsForgettableSetting()
        passwordManager.deleteDatabase()
        NavHostFragment.findNavController(this).navigate(R.id.action_login_self)
    }

    private fun registerPin() {
        enterAndConfirmNewPIN(getString(R.string.REGISTER_PIN)) {
            passwordManager.registerPasswordInDatabase(it)
            onCorrectPinEntered()
        }
    }

    private fun enterAndConfirmNewPIN(initialLabel: String, onPasswordConfirmed: (CharSequence) -> Unit){
        txt_pin_entry.showSoftKeyboard()
        pin_label.text = initialLabel
        txt_pin_entry.text = null
        txt_pin_entry.setOnPinEnteredListener { pass ->
            passwordManager.rememberPassword(pass)
            txt_pin_entry.text = null
            pin_label.text = getString(R.string.RE_ENTER_PIN)
            txt_pin_entry.setOnPinEnteredListener {
                if(!passwordManager.verifyPinIsTheSame(it)) {
                    Toast.makeText(context,"PIN not the same, try again setting a new PIN",Toast.LENGTH_SHORT).show()
                    txt_pin_entry.text = null
                    enterAndConfirmNewPIN(initialLabel, onPasswordConfirmed)
                } else {
                    onPasswordConfirmed(it)
                }
            }
        }
    }

    fun resetPassword() {
        enterAndConfirmNewPIN("NEW PIN") { newPin ->
            passwordManager.changePin(newPin)
            onCorrectPinEntered()
        }
    }

    private fun changePassword() {
        pin_label.text = "Old PIN"
        attemptLogin {loginResult, oldPin ->
            if(loginResult) {
                enterAndConfirmNewPIN("NEW PIN") { newPin ->
                    passwordManager.changePin(newPin)
                    onCorrectPinEntered()
                }
            }
            else {
                onWrongPinEntered()
                changePassword()
            }
        }
    }

    private fun attemptLogin(processLogin: (Boolean, CharSequence)-> Unit){
        txt_pin_entry.showSoftKeyboard()
        txt_pin_entry.setOnPinEnteredListener { text: CharSequence ->
            processLogin(passwordManager.login(text), text)
        }
    }

    private fun onLoginAttempt(isLoginSuccessfully: Boolean) {
        if(isLoginSuccessfully)
            onCorrectPinEntered()
        else
            onWrongPinEntered()
    }

    private fun onCorrectPinEntered() {
        txt_pin_entry.hideSoftKeyboard()

        passwordManager.setIsPinRegistered(true)
        NavHostFragment.findNavController(this).navigate(R.id.action_login_to_mainFragment)
    }

    private fun onWrongPinEntered() {
        Toast.makeText(context, getString(R.string.WRONG_PIN), Toast.LENGTH_SHORT).show()
        txt_pin_entry.isError = true
        txt_pin_entry.text = null

        val waitingPeriodInMilliSeconds = passwordManager.getPeriodToWaitForWrongPin()
        if(waitingPeriodInMilliSeconds > 0)
            showWaitingScreen(waitingPeriodInMilliSeconds)
    }

    private fun showWaitingScreen(waitingPeriodInMilliSeconds: Long) {
        Toast.makeText(context, "DELAY $waitingPeriodInMilliSeconds", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val RESET_PASSWORD_ARG_NAME = "RESET PASSWORD"
        const val RESET_PASSWORD_ARG_VALUE = "RESET PASSWORD"
    }
}
