package com.mihaiapps.securevault

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.mihaiapps.securevault.data.AppDatabaseFactory
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import android.app.Activity
import android.os.Handler
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment

class Login : Fragment() {

    private val appDatabaseFactory: AppDatabaseFactory by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        MainApplication.IsPinEnered = true
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)




        //

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSoftKeyboard(txt_pin_entry)
        txt_pin_entry.setOnPinEnteredListener { text: CharSequence ->
            text as SpannableStringBuilder
            val pass = CharArray(text.length)
            text.getChars(0, text.length,pass, 0)
            appDatabaseFactory.setEncryption(pass)

            if(appDatabaseFactory.isPasswordCorrect()) {
                hideSoftKeyboard(txt_pin_entry)
                NavHostFragment.findNavController(this).navigate(R.id.action_login_to_mainFragment)
            } else {
                Toast.makeText(context, "FAIL", Toast.LENGTH_SHORT).show()
                txt_pin_entry.isError = true
                txt_pin_entry.text = null
            }
        }
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
