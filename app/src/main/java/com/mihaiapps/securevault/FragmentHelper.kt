package com.mihaiapps.securevault

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showSoftKeyboard() {
    if (this.requestFocus()) {
        Handler().postDelayed({
            val imm = this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            val isKeyboardShown = imm!!.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            if(!isKeyboardShown)
                this.showSoftKeyboard()

            Log.d("x", isKeyboardShown.toString())
        }, 2000)
    }
}

fun View.hideSoftKeyboard() {
    val imm = this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    imm!!.hideSoftInputFromWindow(this.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)

}