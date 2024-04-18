package com.example.musicapp.data.network

import com.example.musicapp.data.model.Comment
import com.example.musicapp.data.model.Post
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RetrofitTesting {
    @GET("/comments")
    fun getPostList(@QueryMap data: HashMap<String, Int>): Call<List<Comment>>

    @POST("/posts")
    fun post(@Body post: Post): Call<Post>

}