package com.mihaiapps.googleloginwrapper

import android.content.Intent

interface ExtendableFragment {
    fun setOnActivityResultListener(activityResultDelegate: ActivityResultDelegate)
    fun startActivityForResult(intent: Intent?, requestCode: Int)
}

interface ActivityResultDelegate {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}