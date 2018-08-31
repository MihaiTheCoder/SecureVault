package com.mihaiapps.securevault.bl

import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class GoogleDriveHighLevelAPI(private val activityGoogleDrive: ActivityGoogleDrive){

    fun createFileInRoot(fileName: String, mimeType: String, content: ByteArray): Task<DriveFile> {
        val lowLevelAPITask = getGoogleDriveLowLevelAPI()
        return lowLevelAPITask.onSuccessTask { googleDriveLowLevelAPI: GoogleDriveLowLevelAPI? ->
            googleDriveLowLevelAPI!!.getRootDirectory()!!
        }.onSuccessTask { driveFolder: DriveFolder? ->
            val lowLevelAPI = lowLevelAPITask.result
            lowLevelAPI.createOrUpdateContent(driveFolder!!, fileName, mimeType, content)
        }
    }

    fun createFile(directory: DriveFolder, fileName: String, mimeType:String, content:ByteArray): Task<DriveFile> {
        val result = getGoogleDriveLowLevelAPI()

        return getGoogleDriveLowLevelAPI().onSuccessTask { googleDriveLowLevelAPI: GoogleDriveLowLevelAPI? ->
            googleDriveLowLevelAPI!!.createOrUpdateContent(directory, fileName, mimeType, content)
        }
    }

    private fun getGoogleDriveLowLevelAPI(): Task<GoogleDriveLowLevelAPI> {
        val resourceClientTask = activityGoogleDrive.getDriveResourceClient()
        val driveClientTask = activityGoogleDrive.getDriveClient()
        return Tasks.whenAll(resourceClientTask, driveClientTask).onSuccessTask {
            val resourceClient = resourceClientTask.result
            val driveClient = driveClientTask.result
            Tasks.forResult(GoogleDriveLowLevelAPI(driveClient, resourceClient))
        }
    }

    fun getFileContentFromRoot(fileName: String): Task<String?> {
        val lowLevelAPITask = getGoogleDriveLowLevelAPI()
        return lowLevelAPITask.onSuccessTask {googleDriveLowLevelAPI: GoogleDriveLowLevelAPI? ->
            googleDriveLowLevelAPI!!.getRootDirectory()!!
        }.onSuccessTask { driveFolder: DriveFolder? ->
            lowLevelAPITask.result.getContent(driveFolder!!, fileName)
        }

    }
}