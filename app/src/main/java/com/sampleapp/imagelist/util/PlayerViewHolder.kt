package com.sampleapp.imagelist.util

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.sampleapp.imagelist.R

class PlayerViewHolder : RecyclerView.ViewHolder {

    var mediaContainer: FrameLayout? = null
    var mediaCoverImage: ImageView? = null
    var volumeControl:ImageView? = null
    var progressBar: ProgressBar? = null
    var requestManager: RequestManager? = null
    private var title: TextView? = null
    private var userHandle: TextView? = null
    private var parent: View? = null

    constructor(itemView: View) : super(itemView) {
        parent = itemView
        mediaContainer = itemView.findViewById(R.id.mediaContainer)
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage)
        title = itemView.findViewById(R.id.tvTitle)
        userHandle = itemView.findViewById(R.id.tvUserHandle)
        progressBar = itemView.findViewById(R.id.progressBar)
        volumeControl = itemView.findViewById(R.id.ivVolumeControl)
    }

    fun onBind(mediaObject: MediaObject, requestManager: RequestManager) {
        this.requestManager = requestManager
        parent?.setTag(this)
        title?.text = mediaObject.title
        userHandle?.text = mediaObject.userHandle
//        this.requestManager.load(mediaObject.getCoverUrl()).into(mediaCoverImage)
        mediaCoverImage?.let {
            Glide.with(itemView).load(mediaObject.mediaCoverImgUrl).into(it)
        }
    }
}