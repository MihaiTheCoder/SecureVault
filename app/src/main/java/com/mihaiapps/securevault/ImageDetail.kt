package com.mihaiapps.securevault

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.mihaiapps.googleloginwrapper.ActivityResultDelegate
import com.mihaiapps.googleloginwrapper.ExtendableFragment
import com.mihaiapps.googledriverestapiwrapper.restapi.RestDriveApiHighLevel
import com.mihaiapps.googleloginwrapper.GoogleSignInFactory
import com.mihaiapps.securevault.bl.ACTIVITY_RESULT_CODES
import com.mihaiapps.securevault.bl.ImageModel
import com.mihaiapps.securevault.bl.LocalFileReader
import kotlinx.android.synthetic.main.fragment_image_detail.*
import org.koin.android.ext.android.inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ImageDetail.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ImageDetail.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ImageDetail : Fragment(), com.mihaiapps.googleloginwrapper.ExtendableFragment {
    // TODO: Rename and change types of parameters
    private var image: ImageModel? = null
    private val googleSignInFactory: GoogleSignInFactory by inject()
    private val googleHighLevelRestApi by lazy { RestDriveApiHighLevel(context!!, this, ACTIVITY_RESULT_CODES.REQUEST_CODE_SIGN_IN, googleSignInFactory) }
    private val localFileReader by inject<LocalFileReader>()

    private val listeners = ArrayList<com.mihaiapps.googleloginwrapper.ActivityResultDelegate>()
    override fun setOnActivityResultListener(activityResultDelegate: com.mihaiapps.googleloginwrapper.ActivityResultDelegate) {
        listeners.add(activityResultDelegate)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (listener in listeners)
            listener.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (savedInstanceState != null) {
                image = savedInstanceState.getParcelable(EXTRA_IMAGE)
            }
        }
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = arguments!![EXTRA_IMAGE] as ImageModel
        val imageView = view.findViewById<PhotoView>(R.id.detail_image)

        Glide.with(activity!!)
                .asBitmap()
                .load(image.url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        startPostponedEnterTransition()
                        imageView.setImageBitmap(resource)
                    }
                })

        share_image.setOnClickListener {
            val inputStream = localFileReader.getInputStrem(image.url)

            googleHighLevelRestApi.shareFile(image.name, inputStream, "mihai.petrutiu@qubiz.com" )
        }
    }

    companion object {

        private val EXTRA_IMAGE = "image_item"

        @JvmStatic
        fun newInstance(image: ImageModel): ImageDetail {
            return ImageDetail().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_IMAGE, image)
                }
            }
        }

    }
}
