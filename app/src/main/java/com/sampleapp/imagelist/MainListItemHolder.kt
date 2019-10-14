package com.sampleapp.imagelist

import android.content.ComponentName
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.meme.hwapp.response.Photo
import kotlinx.android.synthetic.main.item_main_list.view.*

class MainListItemHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_list, parent, false)) {

    val photo = itemView.imageMainItem
    val title = itemView.textMainItem
    val video = itemView.vedioMainItem

    private var exoplayerView : SimpleExoPlayerView? = null
    private var exoplayer : SimpleExoPlayer? = null
    private var playbackStateBuilder : PlaybackStateCompat.Builder? = null
    private var mediaSession: MediaSessionCompat? = null

    fun bind(itemPhoto: Photo) {
        initPlayer()

        val imageString = "https://farm${itemPhoto.farm}.staticflickr.com/${itemPhoto.server}/${itemPhoto.id}_${itemPhoto.secret}.jpg"

        if (imageString != null) {
            if (!imageString.isEmpty()) {
                if (itemPhoto.isVideo) {
                    // todo : 비디오 재생 구간
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
    }

    fun initPlayer() {
        val trackSelector = DefaultTrackSelector()
        exoplayer = ExoPlayerFactory.newSimpleInstance(itemView.context, trackSelector)
        exoplayerView?.player = exoplayer

        val userAgent = Util.getUserAgent(itemView.context, "Exo")
        val mediaUri = Uri.parse("asset:///heart_attack.mp3")
        val mediaSource = ExtractorMediaSource(mediaUri, DefaultDataSourceFactory(itemView.context, userAgent), DefaultExtractorsFactory(), null, null)

        exoplayer?.prepare(mediaSource)

        val componentName = ComponentName(itemView.context, "Exo")
        mediaSession = MediaSessionCompat(itemView.context, "ExoPlayer", componentName, null)

        playbackStateBuilder = PlaybackStateCompat.Builder()

        playbackStateBuilder?.setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_FAST_FORWARD)

        mediaSession?.setPlaybackState(playbackStateBuilder?.build())
        mediaSession?.isActive = true
    }
}