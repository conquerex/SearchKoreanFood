package com.sampleapp.imagelist

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sampleapp.imagelist.response.Photo

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

    override fun getItemId(position: Int): Long {
        return images[position].id.toLong()
    }

    fun addImages(imgs: ArrayList<Photo>) {
        for (i in 1..imgs.size) {
            // 동영상 들어갈 Item, 임의로 정의
            if (i == 4) {
                Log.d("MainListAdapter", "* * * isVideo = true")
                imgs[i-1].isVideo = true
            }
            images.add(imgs[i-1])
        }
        notifyItemRangeChanged(images.size - imgs.size, imgs.size)
    }

    fun refreshImages(imgs: ArrayList<Photo>) {
        images = imgs
        notifyDataSetChanged()
    }
}