package com.mihaiapps.googledriverestapiwrapper.restapi

import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.Permission
import java.io.FileOutputStream
import java.io.InputStream

class RestDriveApiLowLevel(private val drive: Drive) {


    fun getRootDirectory(): Drive.Files.List? {
        return drive.Files().list()
    }

    fun downloadById(fileId: String, stream: FileOutputStream) {
        drive.files().get(fileId).executeMediaAndDownloadTo(stream)
    }

    fun downloadByName(parentFolderId: String?, fileName: String, space:String=DEFAULT_SPACE): ByteArray? {
        val existingFile = getFileMetadata(parentFolderId, fileName, space)
        val fileId = existingFile?.id ?: return null
        return drive.files()
                .get(fileId)
                .executeMediaAsInputStream()
                .use { stream -> stream.readBytes() }
    }

    fun uploadByName(parentFolderId: String?, fileName: String, data:ByteArray, space: String= DEFAULT_SPACE): File? {
        val content = ByteArrayContent(CONTENT_TYPE, data)

        return uploadByName(parentFolderId, fileName, content, space)
    }

    fun uploadByName(parentFolderId: String?, fileName: String, file: java.io.File, space: String= DEFAULT_SPACE): File? {
        val content = FileContent(CONTENT_TYPE, file)

        return uploadByName(parentFolderId, fileName, content, space)
    }

    fun uploadByName(parentFolderId: String?, fileName: String, inputStream: InputStream, space: String= DEFAULT_SPACE): File? {
        val content = InputStreamContent(CONTENT_TYPE, inputStream)

        return uploadByName(parentFolderId, fileName, content, space)
    }

    private fun uploadByName(parentFolderId: String?, fileName: String, content: AbstractInputStreamContent, space: String= DEFAULT_SPACE): File? {
        val existingFile = getFileMetadata(parentFolderId, fileName, space)
        if (existingFile != null)
            return drive.files().update(existingFile.id, null, content).execute()

        val newFileMetadata = File()
        newFileMetadata.name = fileName
        setParents(parentFolderId, newFileMetadata, space)
        //newFileMetadata.parents = if (parentFolderId == null) listOf(space) else listOf(space, parentFolderId)

        return drive.files()
                .create(newFileMetadata, content)
                .setFields(UPLOAD_FIELDS)
                .execute()
    }

    fun shareFileWithEmails(file: File, vararg emails: String ): List<Permission> {
        val permissions = emails.map { getReadPermissionToUser(it) }
        return permissions.map { drive.permissions().create(file.id, it).execute() }
    }

    private fun getReadPermissionToUser(email: String) =
            Permission().setType("user").setRole("reader").setEmailAddress(email)!!

    fun list(parentId: String, recursive:Boolean = true): ArrayList<File> {
        val files = ArrayList<File>()
        var result: FileList? = null
        do {
            result = drive.files()
                    .list()
                    .setQ(withParentQuery(parentId))
                    .setFields(NEXT_PAGE_QUERY)
                    .setPageToken(result?.nextPageToken)
                    .execute()
            for( file in result.files) {
                if(file.mimeType == DRIVE_FOLDER && recursive)
                    files.addAll(list(file.id, recursive))
                else
                    files.add(file)
            }

        } while (result?.nextPageToken != null)
        return files

    }

    fun getFileMetadata(parentFolderId: String?, fileName: String, space: String = DEFAULT_SPACE): File? {
        val query =
                if (parentFolderId != null)
                    allConditions(withParentQuery(parentFolderId), fileNameEqualsQuery(fileName))
                else
                    fileNameEqualsQuery(fileName)
        return drive.files()
                .list()
                .setSpaces(space)
                .setQ(query)
                .execute()
                .files
                .firstOrNull()
    }

    fun createFolder(parentFolderId: String?, folderName: String, space: String = DEFAULT_SPACE): File? {
        val fileMetadata = File()
        fileMetadata.name = folderName
        fileMetadata.mimeType = "application/vnd.google-apps.folder"
        setParents(parentFolderId, fileMetadata, space)
        //fileMetadata.parents = if (parentFolderId == null) listOf(space) else listOf(space, parentFolderId)
        return drive.files().create(fileMetadata).setFields(UPLOAD_FIELDS).execute()
    }

    private fun setParents(parentFolderId: String?, fileMetadata: File, space: String) {
        if (parentFolderId != null) {
            fileMetadata.parents = listOf(parentFolderId)//listOf(space, parentFolderId)
        }
    }

    private fun withParentQuery(parentId: String) = "'$parentId' in parents"

    private fun fileNameEqualsQuery(fileName: String) = "name='$fileName'"

    private fun allConditions(vararg conditions: String) = conditions.joinToString(separator = " and ")

    companion object {
        const val CONTENT_TYPE = "application/octet-stream"
        const val DEFAULT_SPACE = "drive"
        const val DRIVE_FOLDER = "application/vnd.google-apps.folder"
        const val NEXT_PAGE_QUERY = "nextPageToken, files(id, name, mimeType, size)"
        const val UPLOAD_FIELDS = "id, parents, name"
    }
}