package com.mihaiapps.securevault

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.mihaiapps.securevault.bl.enc.PasswordManager
import com.mihaiapps.securevault.data.AppDatabase

class Login : Fragment() {
    private val passwordManager: PasswordManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        change_password_btn.setOnClickListener { changePassword() }

        zeroize_btn.setOnClickListener { zeroize() }
    }

    private fun zeroize() {
        passwordManager.setIsPinRegistered(false)
        MainApplication.getContext().deleteDatabase(AppDatabase.NAME)
        NavHostFragment.findNavController(this).navigate(R.id.action_login_self)
    }

    private fun registerPin() {
        enterAndConfirmNewPIN(getString(R.string.REGISTER_PIN)) {
            passwordManager.registerPasswordInDatabase(it)
            onCorrectPinEntered()
        }
    }

    private fun enterAndConfirmNewPIN(initialLabel: String, onPasswordConfirmed: (CharSequence) -> Unit){
        showSoftKeyboard(txt_pin_entry)
        pin_label.text = initialLabel
        txt_pin_entry.text = null
        txt_pin_entry.setOnPinEnteredListener { pass ->
            passwordManager.rememberPassword(pass)
            txt_pin_entry.text = null
            pin_label.text = getString(R.string.RE_ENTER_PIN)
            txt_pin_entry.setOnPinEnteredListener {
                if(!passwordManager.verifyPinIsTheSame(it)) {
                    Toast.makeText(context,"PIN not the same, try again setting a new PIN",Toast.LENGTH_SHORT)
                    txt_pin_entry.text = null
                    enterAndConfirmNewPIN(initialLabel, onPasswordConfirmed)
                } else {
                    onPasswordConfirmed(it)
                }
            }
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
        showSoftKeyboard(txt_pin_entry)
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
        hideSoftKeyboard(txt_pin_entry)
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

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            Handler().postDelayed({
                val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                val isKeyboardShown = imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                if(!isKeyboardShown)
                    showSoftKeyboard(view)

                Log.d("x", isKeyboardShown.toString())
            }, 20)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        imm!!.hideSoftInputFromWindow(view.windowToken,InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    companion object {
    }
}
