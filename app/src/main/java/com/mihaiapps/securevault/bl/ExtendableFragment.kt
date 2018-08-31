package com.mihaiapps.securevault.bl

import android.content.Context
import android.content.Intent

interface ExtendableFragment {
    fun setOnActivityResultListener(activityResultDelegate: ActivityResultDelegate)
    fun startActivityForResult(intent: Intent?, requestCode: Int)
}

interface ActivityResultDelegate {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}