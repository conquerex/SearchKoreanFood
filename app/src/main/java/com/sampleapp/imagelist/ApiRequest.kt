package com.meme.hwapp.network

import com.meme.hwapp.response.ImagesResponse
import com.sampleapp.imagelist.R
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequest {

    @GET("services/rest/")
    fun getImages(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String,
        @Query("method") query: String = "flickr.photos.search",
        @Query("text") text: String = "'Korean Food'",
        @Query("per_page") perPage: String = "10",
        @Query("format") format: String = "json",
        @Query("nojsoncallback") nojsoncallback: String = "1"
    ): Call<ImagesResponse>
}