package com.mihaiapps.securevault.bl.google_drive_android_api

import com.google.android.gms.drive.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class GoogleDriveLowLevelAPI(private val driveClient: DriveClient, private val driveResourceClient: DriveResourceClient) {

    fun getRootDirectory(): Task<DriveFolder>? {
        return driveResourceClient.rootFolder
    }

    fun createOrUpdateContent(directory: DriveFolder, fileName: String, mimeType:String, content:ByteArray): Task<DriveFile> {
        return getDriveFile(directory, fileName).onSuccessTask {driveFile: DriveFile? ->
            if(driveFile == null)
                createContent(directory, fileName, mimeType, content)
            else
                updateFile(driveFile,fileName, mimeType, content)
        }
    }

    fun getDriveFile(directory: DriveFolder, fileName: String): Task<DriveFile?> {
        return driveResourceClient.listChildren(directory).onSuccessTask { metadataBuffer: MetadataBuffer? ->
            var result: Task<DriveFile?> = Tasks.forResult(null)
            for(item in metadataBuffer!!) {
                if(item.originalFilename == fileName) {
                    result = Tasks.forResult(item.driveId.asDriveFile())
                }
            }
            result
        }
    }

    fun createContent(directory: DriveFolder, fileName: String, mimeType:String, content:ByteArray): Task<DriveFile> {
        return driveResourceClient.createContents().onSuccessTask { driveContents: DriveContents? ->
            driveContents?.outputStream.use {
                it?.write(content)
            }
            driveResourceClient.createFile(directory,
                    MetadataChangeSet.Builder()
                            .setTitle(fileName)
                            .setMimeType(mimeType)
                            .build(), driveContents)
        }
    }

    fun updateFile(driveFile: DriveFile, fileName: String, mimeType: String,content: ByteArray): Task<DriveFile> {
        val openFile = driveResourceClient.openFile(driveFile, DriveFile.MODE_WRITE_ONLY)
        return openFile.onSuccessTask { driveContents: DriveContents? ->
            driveContents!!.outputStream.use { it.write(content) }

            val client = driveResourceClient.asGoogleApiClient()!!

            driveResourceClient.commitContents(driveContents,
                    MetadataChangeSet
                            .Builder()
                            .setTitle(fileName)
                            .setMimeType(mimeType)
                            .build(),ExecutionOptions.Builder()
                    .setConflictStrategy(ExecutionOptions.CONFLICT_STRATEGY_OVERWRITE_REMOTE)
                    .build())
        }.onSuccessTask { Tasks.forResult(driveFile) }
    }

    fun getContent(driveFolder: DriveFolder, fileName: String): Task<String?> {
        return getDriveFile(driveFolder, fileName).onSuccessTask {driveFile: DriveFile? ->
            if(driveFile == null)
                Tasks.forResult<String?>(null)
            else {
                val openFileTask = driveResourceClient.openFile(driveFile,DriveFile.MODE_READ_ONLY)
                val content = openFileTask.onSuccessTask { driveContents: DriveContents? ->
                    Tasks.forResult(driveContents!!.inputStream.reader().readText())
                }
                content
            }
        }

    }
}