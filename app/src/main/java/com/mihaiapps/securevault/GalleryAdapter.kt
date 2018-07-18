package com.mihaiapps.securevault

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mihaiapps.securevault.bl.ImageModel
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions


class GalleryAdapter(private val data: List<ImageModel>,
                     private val listener: (imageModel: ImageModel, position: Int, imageView: ImageView) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false)
        return MyItemHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val galleryHolder = (holder as MyItemHolder)
        val imageModel = data[position]

        Glide.with(galleryHolder.img.context)
                .load(imageModel.url)
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions().crossFade())
                .apply(RequestOptions().override(200, 200).diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(galleryHolder.img)

        ViewCompat.setTransitionName(holder.img,  imageModel.name)

        holder.itemView.setOnClickListener { listener(data[position], position, holder.itemView as ImageView) }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class MyItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val img: ImageView = itemView.findViewById(R.id.galleryImage)
}