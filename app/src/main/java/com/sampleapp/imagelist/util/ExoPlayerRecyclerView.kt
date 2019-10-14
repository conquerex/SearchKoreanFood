package com.sampleapp.imagelist.util


import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.sampleapp.imagelist.MainListItemHolder

import com.sampleapp.imagelist.R
import com.sampleapp.imagelist.response.Photo
import java.util.*

class ExoPlayerRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }
    
    private val TAG = "ExoPlayerRecyclerView"
    private val AppName = "Android ExoPlayer"

    private var volumeControl: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var mediaContainer: FrameLayout? = null
    private var videoSurfaceView: PlayerView? = null
    private var videoPlayer: SimpleExoPlayer? = null

    // Media List
//    private var mediaObjects = ArrayList<MediaObject>()
    private var photoList = ArrayList<Photo>()
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var playPosition = -1
    private var isVideoViewAdded: Boolean = false
    private var requestManager: RequestManager? = null
    // controlling volume state
    private var volumeState: VolumeState? = null
    private var videoViewClickListener = OnClickListener { toggleVolume() }


    fun init(context: Context) {
//        this.context = context.getApplicationContext()
        var display = (Objects.requireNonNull(this.context.getSystemService(Context.WINDOW_SERVICE)) as WindowManager).defaultDisplay
        var point = Point()
        display.getSize(point)

        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y

        videoSurfaceView = PlayerView(this.context)
        videoSurfaceView!!.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)

        var bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        var videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        var trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        //Create the player using ExoPlayerFactory
        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        // Disable Player Control
        videoSurfaceView!!.setUseController(false)
        // Bind the player to the view.
        videoSurfaceView!!.setPlayer(videoPlayer)
        // Turn on Volume
        setVolumeControl(VolumeState.ON)

        addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // SCROLL_STATE_IDLE : 스크롤이 정지된 상태
                if (newState == SCROLL_STATE_IDLE) {
//                    if (!recyclerView.canScrollVertically(1)) {
//                        playVideo(true)
//                    } else {
//                        playVideo(false)
//                    }
                }
            }
        })

        addOnChildAttachStateChangeListener(object: OnChildAttachStateChangeListener {

            override fun onChildViewAttachedToWindow(view: View) {
                // nothing
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent!!.equals(view)) {
                    resetVideoView()
                }
            }
        })

        videoPlayer?.addListener(object: Player.EventListener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                super.onPlayerStateChanged(playWhenReady, playbackState)

                when {
                    playbackState == Player.STATE_BUFFERING -> {
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.")
                        if (progressBar != null) {
                            progressBar!!.setVisibility(VISIBLE)
                        }
                    }

                    playbackState == Player.STATE_ENDED -> {
                        Log.d(TAG, "onPlayerStateChanged: Video ended.")
                        videoPlayer!!.seekTo(0)
                    }

                    playbackState == Player.STATE_IDLE -> {
                        //
                    }

                    playbackState == Player.STATE_READY -> {
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.")
                        if (progressBar != null) {
                            progressBar!!.setVisibility(GONE)
                        }
                        if (!isVideoViewAdded) {
                            addVideoView()
                        }
                    }

                    else -> {
                        //
                    }
                }
            }
        })
    }

    fun playVideo(isEndOfList: Boolean) {

        var targetPosition = 0

        // 마지막 위치인지 확인
        if (!isEndOfList) {
            var startPosition: Int = (Objects.requireNonNull(getLayoutManager()) as LinearLayoutManager).findFirstVisibleItemPosition()
            var endPosition : Int = (getLayoutManager() as LinearLayoutManager).findLastVisibleItemPosition()
            Log.d(TAG, "* * * start  : " + startPosition)
            Log.d(TAG, "* * * end    : " + endPosition)
            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return
            }

            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                var startPositionVideoHeight : Int = getVisibleVideoSurfaceHeight(startPosition)
                var endPositionVideoHeight : Int = getVisibleVideoSurfaceHeight(endPosition)
                targetPosition = if (startPositionVideoHeight > endPositionVideoHeight) {
                    startPosition
                } else {
                    endPosition
                }
            } else {
                targetPosition = startPosition
            }
        } else {
            targetPosition = photoList.size - 1
        }

        // video is already playing so return
        if (targetPosition == playPosition) {
            return
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition
        if (videoSurfaceView == null) {
            return
        }

        // remove any old surface views from previously playing videos
        videoSurfaceView!!.setVisibility(INVISIBLE)
        removeVideoView(videoSurfaceView!!)

        var currentPosition: Int
                = targetPosition - (Objects.requireNonNull(getLayoutManager()) as LinearLayoutManager).findFirstVisibleItemPosition()

        Log.d(TAG, "* * * target : " + targetPosition)
        Log.d(TAG, "* * * current: " + currentPosition)

        var child : View = getChildAt(currentPosition)

        Log.d(TAG, "* * * null chk : " + (child.getTag() == null))

        var holder : MainListItemHolder = child.tag as MainListItemHolder

//        mediaCoverImage = holder.mediaCoverImage
        progressBar = holder.progressBar
        volumeControl = holder.volumeControl
        viewHolderParent = holder.itemView
        mediaContainer = holder.mediaContainer

        videoSurfaceView!!.setPlayer(videoPlayer)
        viewHolderParent!!.setOnClickListener(videoViewClickListener)

        var dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, AppName))
//        var mediaUrl: String = mediaObjects.get(targetPosition).mediaUrl
        var mediaUrl = "https://www.radiantmediaplayer.com/media/bbb-360p.mp4"
        if (mediaUrl != null) {
            var videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mediaUrl))
            videoPlayer?.prepare(videoSource)
            videoPlayer?.setPlayWhenReady(true)
        }
    }

    fun getVisibleVideoSurfaceHeight(playPosition: Int) : Int {
        var at: Int
                = playPosition - (Objects.requireNonNull(getLayoutManager()) as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at)

        var child : View = getChildAt(at)
        if (child == null) {
            return 0
        }

        var location = IntArray(2)

        child.getLocationInWindow(location)

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight
        } else {
            return screenDefaultHeight - location[1]
        }
    }

    // Remove the old player
    fun removeVideoView(videoView: PlayerView) {
        if (videoView.getParent() == null) {
            return
        }
        var parent : ViewGroup = videoView.getParent() as ViewGroup

        var index: Int = parent.indexOfChild(videoView)
        if (index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
            viewHolderParent?.setOnClickListener(null)
        }
    }

    fun addVideoView() {
        mediaContainer?.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView?.requestFocus()
        videoSurfaceView?.setVisibility(VISIBLE)
        videoSurfaceView?.setAlpha(1f)
    }

    fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView!!)
            playPosition = -1
            videoSurfaceView?.setVisibility(INVISIBLE)
        }
    }

    fun releasePlayer() {

        if (videoPlayer != null) {
            videoPlayer?.release()
            videoPlayer = null
        }

        viewHolderParent = null
    }

    fun onPausePlayer() {
        if (videoPlayer != null) {
            videoPlayer?.stop(true)
        }
    }

    fun toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.")
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.")
                setVolumeControl(VolumeState.OFF)
            }
        }
    }

    fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            videoPlayer?.setVolume(0f)
            animateVolumeControl()
        } else if (state == VolumeState.ON) {
            videoPlayer?.setVolume(1f)
            animateVolumeControl()
        }
    }

    fun animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl?.bringToFront()
            if (volumeState == VolumeState.OFF) {
                requestManager!!.load(R.drawable.ic_volume_off_black_24dp).into(volumeControl!!)
            } else if (volumeState == VolumeState.ON) {
                requestManager!!.load(R.drawable.ic_volume_up_black_24dp).into(volumeControl!!)
            }
            volumeControl!!.animate().cancel()

            volumeControl!!.setAlpha(1f)

            volumeControl!!.animate()
                .alpha(0f)
                .setDuration(600).setStartDelay(1000)
        }
    }

    fun setMediaObjects(mediaObjects: ArrayList<MediaObject>) {
//        this.mediaObjects = mediaObjects
    }

    fun setPhotos(photos: ArrayList<Photo>) {
        this.photoList = photos
    }

    enum class VolumeState {
        ON, OFF
    }
}