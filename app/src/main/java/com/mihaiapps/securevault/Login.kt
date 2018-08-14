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
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.mihaiapps.securevault.R.id.pin_label
import com.mihaiapps.securevault.bl.enc.PasswordManager

class Login : Fragment() {
    private val passwordManager: PasswordManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        MainApplication.IsPinEnered = true

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(passwordManager.getIsPinRegistered()) {
            if(passwordManager.getIsPasswordForgettable()) {
                loginPinForgettablePassword()
            }
            else {
                loginPinUnforgettablePassword()
            }
            pin_label.text = getString(R.string.LOGIN_PIN)
        }
        else {
            registerPin()
        }
    }

    private fun registerPin() {
        showSoftKeyboard(txt_pin_entry)
        pin_label.text = getString(R.string.REGISTER_PIN)
        txt_pin_entry.setOnPinEnteredListener { text: CharSequence ->
            passwordManager.rememberPassword(text)
            txt_pin_entry.text = null
            pin_label.text = getString(R.string.RE_ENTER_PIN)
            txt_pin_entry.setOnPinEnteredListener {text: CharSequence ->
                if(passwordManager.verifyPinIsTheSame(text)) {
                    passwordManager.registerPasswordInDatabase(text)
                    onCorrectPinEntered()
                } else {
                    onWrongPinEntered()
                }
            }
        }
    }

    private fun loginPinForgettablePassword() {
        showSoftKeyboard(txt_pin_entry)
        txt_pin_entry.setOnPinEnteredListener { text:CharSequence ->
            isPinCorrectlyRegisteredInDb(text)
        }
    }

    private fun loginPinUnforgettablePassword() {
        showSoftKeyboard(txt_pin_entry)
        txt_pin_entry.setOnPinEnteredListener { text: CharSequence ->
            passwordManager.initDbWithPassword(convertToCharArray(text))
            if (passwordManager.isDatabaseCorrectlyDecrypted()) {
                isPinCorrectlyRegisteredInDb(text)
            } else
                onWrongPinEntered()
        }
    }

    private fun isPinCorrectlyRegisteredInDb(text: CharSequence) {
        if (passwordManager.login(text))
            onCorrectPinEntered()
        else {
            onWrongPinEntered()
        }
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

    private fun convertToCharArray(text: CharSequence): CharArray {
        text as SpannableStringBuilder
        val pass = CharArray(text.length)
        text.getChars(0, text.length, pass, 0)
        return pass
    }

    fun showSoftKeyboard(view: View) {
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

    fun hideSoftKeyboard(view: View) {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        imm!!.hideSoftInputFromWindow(view.windowToken,InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    companion object {
    }
}
