package com.sampleapp.imagelist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meme.hwapp.network.ApiRequest
import com.meme.hwapp.network.NetworkSetting
import com.meme.hwapp.response.ImagesResponse
import com.meme.hwapp.response.Photos
import com.sampleapp.imagelist.response.Photo
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    var apiRequest = NetworkSetting.getClient().create(ApiRequest::class.java)
    var page = 1
    var loading = true
    var listAdapter = MainListAdapter()
    var photoList = ArrayList<Photo>()
    var videoOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listAdapter.setHasStableIds(true)
        getImages(page)
    }

    override fun onPause() {
        super.onPause()
        // todo 앱이 백그라운드로 나올 때, 플레이어 중지
        if (videoOn) {
//            listAdapter.stopPlayer()
        }
    }

    fun getImages(page: Int) {
        apiRequest.getImages(page).enqueue(object : Callback<ImagesResponse> {
            override fun onFailure(call: Call<ImagesResponse>, t: Throwable) {
                Log.d(TAG, "* * * onFailure")
                Log.d(TAG, "* * * ${t.message}")
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
        photoList.addAll(photos.photo)

//        listMain.setPhotos(photoList)
        listMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 마지막 페이지인지 체크
                if (photos.page * (photos.perpage - 1) > photos.total) {
                    return
                }

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = linearLayoutManager.getItemCount()
                val pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                if (loading) {
                    if (pastVisiblesItems >= totalItemCount - 3) {
                        loading = false
                        getImages(page)
                    }
                }
            }

            /**
             * todo 영상이 화면 바깥으로 나갔으나 영상은 몇초 후 종료가 됨
             */
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var videoOnTemp = false

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

                    var targetPosition = 0
                    var startPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                    var endPosition : Int = linearLayoutManager.findLastVisibleItemPosition()

                    if (startPosition <= endPosition) {
//                        var startPositionVideoHeight : Int = listMain.getVisibleVideoSurfaceHeight(startPosition)
//                        var endPositionVideoHeight : Int = listMain.getVisibleVideoSurfaceHeight(endPosition)

                        loop@ for (i in startPosition..endPosition) {
                            Log.d(TAG, "* * * isVideo  : ${i} // ${photoList[i].isVideo}")
                            if (photoList[i].isVideo) {
                                targetPosition = i // 영상이 있으면 위치 번호를 매겨줌
                                videoOnTemp = true
                                break@loop
                            } else {
                                targetPosition = -1
                            }
                        }
                    } else {
                        targetPosition = -1
                    }

                    // onScrolled 내부의 로딩 분기처리는 아래와 합치는 것이 좋다.
                    // listAdapter를 비슷한 타이밍에 두번 호출하는 경우를 막을 필요가 있다.
                    if (loading) {
                        for (i in 0..photoList.size-1) {
                            photoList[i].videoOn = false
                            if (photoList[i].isVideo) {
                                if (i == targetPosition) {
                                    photoList[i].videoOn = true
                                    Log.d(TAG, "* * * videoOn true : ${i}")
                                }
                            }
                        }
                        Log.d(TAG, "* * * videoOn     : ${videoOn}")
                        Log.d(TAG, "* * * videoOnTemp : ${videoOnTemp}")
                        if (videoOn == videoOnTemp) {
                            return
                        } else {
                            videoOn = videoOnTemp
                            listAdapter.refreshImages(photoList)
                        }
                    }

                }
            }
        })
    }
}
