package com.mihaiapps.securevault

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.mihaiapps.securevault.bl.ImageModel
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.mihaiapps.securevault.controls.checkablebutton.GalleryMultiselect


class GalleryAdapter(private val data: List<ImageModel>,
                     private val listener: (imageModel: ImageModel, position: Int, imageView: ImageView) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false)
        return GalleryImageHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val galleryHolder = (holder as GalleryImageHolder)
        val imageModel = data[position]
        val context = holder.itemView.context

        Glide.with(context)
                .load(imageModel.url)
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions().crossFade())
                .apply(RequestOptions().override(200, 200).diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(galleryHolder.img)

        ViewCompat.setTransitionName(holder.img, imageModel.name)

        holder.itemView.setOnLongClickListener {
            GalleryImageHolder.galleryMultiselect.onLongClick(holder, showToolbar = {
                Toast.makeText(context, "Show toolbar", Toast.LENGTH_SHORT).show()
            })
            true }


        holder.itemView.setOnClickListener {
            GalleryImageHolder.galleryMultiselect.onClick(holder, navigateToImage = {
                listener(data[position], position, galleryHolder.img)
            } )
        }
    }



    override fun getItemCount(): Int {
        return data.size
    }
}

class GalleryImageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val img: ImageView = itemView.findViewById(R.id.galleryImage)
    val checkedImg: ImageView = itemView.findViewById(R.id.image_checked)
    val uncheckedImg: ImageView = itemView.findViewById(R.id.image_unchecked)

    init {
        GalleryImageHolder.galleryMultiselect.items.add(this)

    }

    companion object {
        val galleryMultiselect = GalleryMultiselect()
    }
}