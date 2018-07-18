package com.mihaiapps.securevault

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.mihaiapps.securevault.bl.ImageModel

 const val ARG_EXTRA_INITIAL_POS = "initial_pos"
 const val ARG_EXTRA_IMAGES = "images"

class GalleryViewPagerFragment : Fragment() {
    var currentItem: Int? = null
    var images: ArrayList<ImageModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentItem = it.getInt(ARG_EXTRA_INITIAL_POS)
            images = it.getParcelableArrayList<ImageModel>(ARG_EXTRA_IMAGES)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val galleryPagerAdapter = GalleryPagerAdapter(childFragmentManager,images!!)
        val viewPager =view.findViewById<ViewPager>(R.id.image_view_pager)
        viewPager.adapter = galleryPagerAdapter
        viewPager.currentItem = currentItem!!
    }

    companion object {
        @JvmStatic
        fun newInstance(current: Int, images: ArrayList<ImageModel>) =
                GalleryViewPagerFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_EXTRA_INITIAL_POS, current)
                        putParcelableArrayList(ARG_EXTRA_IMAGES, images)
                    }
                }

    }
}
