package com.sampleapp.imagelist

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.sampleapp.imagelist.response.Photo
import kotlinx.android.synthetic.main.item_main_list.view.*


class MainListItemHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_list, parent, false)) {

    val photo = itemView.imageMainItem
    val title = itemView.textMainItem
    val video = itemView.vedioMainItem

    private var playWhenReady = true
    private var exoplayer : SimpleExoPlayer? = null

    fun bind(itemPhoto: Photo) {
        initializePlayer(itemPhoto)

        val imageString = "https://farm${itemPhoto.farm}.staticflickr.com/${itemPhoto.server}/${itemPhoto.id}_${itemPhoto.secret}.jpg"

        if (!imageString.isEmpty()) {
            if (itemPhoto.isVideo) {
                title.text = "Test your streaming URL"
                photo.visibility = View.GONE
                video.visibility = View.VISIBLE
            } else {
                Glide.with(itemView).load(imageString).into(photo)
                title.text = itemPhoto.title
                photo.visibility = View.VISIBLE
                video.visibility = View.GONE
            }
        } else {
            photo.visibility = View.GONE
        }
    }

    private fun initializePlayer(itemPhoto: Photo) {
        val sample = "https://www.radiantmediaplayer.com/media/bbb-360p.mp4"

        if (exoplayer == null) {
            exoplayer = ExoPlayerFactory.newSimpleInstance(itemView.context)
            //플레이어 연결
            video?.setPlayer(exoplayer)
        }

        val mediaSource = buildMediaSource(Uri.parse(sample))
        //prepare
        exoplayer?.prepare(mediaSource, true, false)
        //start,stop
        if (!itemPhoto.videoOn) {
//            exoplayer?.stop(true)
            exoplayer?.setPlayWhenReady(false)
        } else {
            exoplayer?.setPlayWhenReady(true)
        }
    }

    // 네트워크에 있는 미디어 파일을 포맷별 Play가 가능하도록 객체 생성
    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent = Util.getUserAgent(itemView.context, itemView.context.getString(R.string.app_name))
        return if (uri.lastPathSegment!!.contains("mp3") || uri.lastPathSegment!!.contains("mp4")) {
            ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(itemView.context, userAgent)).createMediaSource(uri)
        }
    }
}