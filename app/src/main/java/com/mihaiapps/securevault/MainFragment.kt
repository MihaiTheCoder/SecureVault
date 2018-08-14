package com.mihaiapps.securevault

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.github.florent37.runtimepermission.RuntimePermission.askPermission
import com.mihaiapps.securevault.bl.LocalFileReader
import com.mihaiapps.securevault.bl.enc.EncryptUtils
import com.mihaiapps.securevault.bl.enc.EncryptedFileManager
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import java.io.File


import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {
    private val encryptedFileManager: EncryptedFileManager by inject()
    private val localFileReader: LocalFileReader by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if(!MainApplication.IsPinEnered) {
            NavHostFragment.findNavController(this).navigate(R.id.action_mainFragment_to_login)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val viewGalleryButton: ImageButton = view.findViewById(R.id.view_gallery)
        viewGalleryButton.setOnClickListener{ _ ->
            NavHostFragment.findNavController(this).navigate(R.id.action_mainFragment_to_gallery)
        }

        val takePhotoButton: ImageButton = view.findViewById(R.id.import_photo)
        takePhotoButton.setOnClickListener{_ -> dispatchTakePictureIntent()}

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        import_images_btn.setOnClickListener { _ ->
            askPermission(this)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .onAccepted { _ ->
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_IMAGE)
                    }.ask()

        }
    }

    private lateinit var mCurrentPhotoPath: String

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                localFileReader.getPicturesDirectory()      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if(takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            val photoFile = createImageFile()

            val photoURI = FileProvider.getUriForFile(context!!,FILE_PROVIDER_AUTHORITH, photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            encryptedFileManager.encryptFile(mCurrentPhotoPath,
                    mCurrentPhotoPath + EncryptUtils.FILE_EXTENSION,
                    true)
        }
        if(requestCode == REQUEST_SELECT_IMAGE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                val clipData = data.clipData
                if(clipData != null) {
                    val count = clipData.itemCount
                    for (i in 0 until count) {
                        val imageUri = clipData.getItemAt(i).uri
                        encryptLocalImage(imageUri)
                    }
                } else if(data.data != null) {
                    encryptLocalImage(data.data!!)

                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun encryptLocalImage(imageUri: Uri) {

        val encryptedFilePath = localFileReader.getAppsImageDirectory(imageUri) +
                EncryptUtils.FILE_EXTENSION

        val path = localFileReader.getRealPathFromURI(imageUri)

        encryptedFileManager.encryptFile(path!!, encryptedFilePath, false)
    }

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
        const val REQUEST_SELECT_IMAGE = 2
        const val FILE_PROVIDER_AUTHORITH = "com.mihaiapps.securevault.fileprovider"

    }
}
