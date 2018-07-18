package com.mihaiapps.securevault

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mihaiapps.securevault.bl.ImageModel

class GalleryPagerAdapter(fm: FragmentManager,
                          private val images: ArrayList<ImageModel>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val image = images[position]
        return ImageDetail.newInstance(image)
    }

    override fun getCount(): Int {
        return images.size
    }
}