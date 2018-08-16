package com.mihaiapps.securevault.controls.checkablebutton

import android.view.View
import com.mihaiapps.securevault.GalleryImageHolder
import kotlin.collections.ArrayList

class GalleryMultiselect {
    val items = ArrayList<GalleryImageHolder>()
    val selectedItems = ArrayList<GalleryImageHolder>()
    var isMultiSelectActivated = false

    fun toggleCheckbox(galleryImageHolder: GalleryImageHolder) {
        val isChecked = galleryImageHolder.checkedImg.visibility == View.VISIBLE

        if (isChecked) {
            galleryImageHolder.checkedImg.visibility = View.INVISIBLE
            galleryImageHolder.uncheckedImg.visibility = View.VISIBLE
            selectedItems.remove(galleryImageHolder)
        } else {
            galleryImageHolder.checkedImg.visibility = View.VISIBLE
            galleryImageHolder.uncheckedImg.visibility = View.INVISIBLE
            selectedItems.add(galleryImageHolder)
        }
    }

    fun onLongClick(galleryImageHolder: GalleryImageHolder, showToolbar: () -> Unit) {
        if (isMultiSelectActivated)
            return

        isMultiSelectActivated = true
        for (item in items) {
            if (item != galleryImageHolder) {
                item.uncheckedImg.visibility = View.VISIBLE
            } else {
                item.checkedImg.visibility = View.VISIBLE
            }
        }
        showToolbar()

    }

    fun onClick(galleryImageHolder: GalleryImageHolder, navigateToImage:() -> Unit) {
        if(isMultiSelectActivated) {
            toggleCheckbox(galleryImageHolder)
        } else {
            navigateToImage()
        }
    }

    fun onBackPressed(navigateBack: () -> Unit, hideToolbar: () -> Unit) {
        if(!isMultiSelectActivated) {
            navigateBack()
        } else {
            isMultiSelectActivated = false
            hideToolbar()
            for (item in items) {
                item.uncheckedImg.visibility = View.INVISIBLE
                item.checkedImg.visibility = View.INVISIBLE
            }
        }
    }
}