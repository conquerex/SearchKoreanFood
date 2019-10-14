package com.sampleapp.imagelist

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.meme.hwapp.response.Photo

class MainListAdapter : RecyclerView.Adapter<MainListItemHolder>() {

    var images = ArrayList<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListItemHolder {
        return MainListItemHolder(parent)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: MainListItemHolder, position: Int) {
        holder.bind(images[position])
    }

    fun addImages(imgs: ArrayList<Photo>) {
        Log.d("MainListAdapter", "* * * size : ${imgs.size}")
        var i = 0
        for (img in imgs) {
            // 동영상 들어갈 Item, 임의로 정의
            if (i % 6 == 0) {
                img.isVideo = true
            }
            images.add(img)
        }
        notifyItemRangeChanged(images.size - imgs.size, imgs.size)
    }
}