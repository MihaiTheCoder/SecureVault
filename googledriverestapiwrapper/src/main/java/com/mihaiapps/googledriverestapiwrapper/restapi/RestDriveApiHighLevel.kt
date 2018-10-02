package com.mihaiapps.googledriverestapiwrapper.restapi

import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.api.services.drive.model.File
import java.io.InputStream

class RestDriveApiHighLevel(private val context: Context,
                            private val extendableFragment: com.mihaiapps.googleloginwrapper.ExtendableFragment,
                            private val signInCode: Int) {
    private val restApiLowLevelTask  by lazy { RestDriveApiLowLevelFactory(context, extendableFragment, signInCode).get() }
    val mHandlerThread = HandlerThread("HandlerThread")

    private val mainHandler by lazy { Handler(mHandlerThread.looper) }

    init {
        mHandlerThread.start()
    }

    fun shareFile(fileName: String, inputStream: InputStream, vararg emails: String): Task<File?> {
        return useClientAsync {restDriveApiLowLevel ->
            val sharedFile = shareFile(restDriveApiLowLevel, fileName, inputStream, emails)
            Tasks.forResult(sharedFile)
        }
    }

    private fun shareFile(restDriveApiLowLevel: RestDriveApiLowLevel, fileName: String, inputStream: InputStream, emails: Array<out String>): File? {
        try {
            val folderId = getUploadsFolder(restDriveApiLowLevel)
            val upload = restDriveApiLowLevel.uploadByName(folderId, fileName, inputStream)
            if (upload != null) {
                restDriveApiLowLevel.shareFileWithEmails(upload, *emails)
            }
            return restDriveApiLowLevel.getFileMetadata(folderId, fileName)

        } catch (e: Exception) {
            Log.e("RestDriveApiHighLevel", "Error while sharing the file", e)
            throw e
        }
    }

    private fun <TResult> useClientAsync(useClientFunc: (RestDriveApiLowLevel) -> Task<TResult>): Task<TResult> {
        val completionSource = TaskCompletionSource<Task<TResult>>()

        restApiLowLevelTask.onSuccessTask {
            AsyncTask.execute {
                completionSource.setResult(useClientFunc(it!!))
            }
            completionSource.task
        }
        return completionSource.task.onSuccessTask { task: Task<TResult>? -> task!! }
    }

    private fun getUploadsFolder(restDriveApiLowLevel: RestDriveApiLowLevel): String {
        val file = restDriveApiLowLevel.getFileMetadata(null, SECURE_VAULT_SHARE_FOLDER)
        return if(file != null) {
            file.id
        } else {
            restDriveApiLowLevel.createFolder(null, SECURE_VAULT_SHARE_FOLDER,"")!!.id
        }
    }

companion object {
    const val SECURE_VAULT_SHARE_FOLDER = "SecureVaultShareFolder"
}
}