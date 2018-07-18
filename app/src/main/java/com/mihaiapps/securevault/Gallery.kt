package com.mihaiapps.securevault

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mihaiapps.securevault.bl.ImageModel
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.NavHostFragment

class Gallery : Fragment() {

    var mAdapter: GalleryAdapter? = null
    var mRecyclerView: RecyclerView? = null

    var data: ArrayList<ImageModel> = ArrayList()

    var imgs = arrayOf("https://images.unsplash.com/photo-1444090542259-0af8fa96557e?q=80&fm=jpg&w=1080&fit=max&s=4b703b77b42e067f949d14581f35019b",
            "https://images.unsplash.com/photo-1439546743462-802cabef8e97?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://images.unsplash.com/photo-1441155472722-d17942a2b76a?q=80&fm=jpg&w=1080&fit=max&s=80cb5dbcf01265bb81c5e8380e4f5cc1",
            "https://images.unsplash.com/photo-1437651025703-2858c944e3eb?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://images.unsplash.com/photo-1431538510849-b719825bf08b?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://images.unsplash.com/photo-1434873740857-1bc5653afda8?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://images.unsplash.com/photo-1439396087961-98bc12c21176?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://images.unsplash.com/photo-1433616174899-f847df236857?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://images.unsplash.com/photo-1438480478735-3234e63615bb?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://images.unsplash.com/photo-1438027316524-6078d503224b?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
            "https://i.imgur.com/r1Yulz0.jpg",
            "https://i.imgur.com/K5jim8J.jpg",
            "https://i.imgur.com/vt1Bu3m.jpg",
            "https://i.imgur.com/dma96XC.jpg",
            "https://i.imgur.com/oPGJUzw.jpg",
            "https://i.imgur.com/1vH49b6.jpg",
            "https://i.imgur.com/z6iTu7s.jpg",
            "https://i.imgur.com/lSXuUuD.jpg"
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for ((index, img) in imgs.withIndex()) {
            data.add(ImageModel("Image$index", img))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        val recylerView: RecyclerView = view.findViewById(R.id.rv_images)
        recylerView.layoutManager = GridLayoutManager(this.context, 3)
        recylerView.setHasFixedSize(true)
        mAdapter =GalleryAdapter(data) { _, position, imageView ->
            val args = Bundle().apply {
                putInt(ARG_EXTRA_INITIAL_POS, position)
                putParcelableArrayList(ARG_EXTRA_IMAGES, data)
            }
            NavHostFragment.findNavController(this).navigate(R.id.action_gallery_to_galleryViewPagerFragment, args)


            Log.d("mainActivity", "addOnItemTouchListener")
        }
        recylerView.adapter =mAdapter
        mRecyclerView = recylerView

        return view
    }

    companion object {
        val TAG = Gallery::class.java.simpleName!!
    }
}