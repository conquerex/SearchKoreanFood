package com.sampleapp.imagelist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.meme.hwapp.network.ApiRequest
import com.meme.hwapp.network.NetworkSetting
import com.meme.hwapp.response.ImagesResponse
import com.meme.hwapp.response.Photos
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var apiRequest = NetworkSetting.getClient().create(ApiRequest::class.java)
    var page = 1
    var loading = true
    var listAdapter = MainListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getImages(page)
    }

    fun getImages(page: Int) {
        apiRequest.getImages(page).enqueue(object : Callback<ImagesResponse> {
            override fun onFailure(call: Call<ImagesResponse>, t: Throwable) {
                Log.d("MainRepository", "* * * onFailure")
                Log.d("MainRepository", "* * * ${t.message}")
            }

            override fun onResponse(call: Call<ImagesResponse>, response: Response<ImagesResponse>) {
                response.body()?.let {
                    setAdapter(it.photos)
                    this@MainActivity.page = page + 1
                    loading = true
                }
            }
        })
    }


    private fun setAdapter(photos: Photos) {
        if (photos.page == 1) {
            listMain.adapter = listAdapter
            listMain.layoutManager = LinearLayoutManager(this)
            listMain.setHasFixedSize(false)
        }

        listAdapter.addImages(photos.photo)


        listMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 마지막 페이지인지 체크
                if (photos.page * (photos.perpage - 1) > photos.total) {
                    return
                }

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = linearLayoutManager.getItemCount();
                val pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                if (loading) {
                    if (pastVisiblesItems >= totalItemCount - 3) {
                        loading = false
                        getImages(page)
                    }
                }
            }
        })
    }
}
