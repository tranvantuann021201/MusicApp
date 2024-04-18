package com.example.musicapp.data.repostirory

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.Constants.DOMAIN
import com.example.musicapp.data.dao.SongDao
import com.example.musicapp.data.database.AppDatabase
import com.example.musicapp.data.model.Comment
import com.example.musicapp.data.model.Post
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.network.RetrofitTesting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongRepository(application: Application) {
    private val songDao: SongDao
    private val retrofit: Retrofit
    private val apiService: RetrofitTesting

    init {
        val appDatabase = AppDatabase.getInstance(application)
        songDao = appDatabase.songDao()
        retrofit = Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(RetrofitTesting::class.java)
    }

    fun getListComment(postId: Int) {
        try {
            val comments = MutableLiveData<List<Comment>>()
            val data = HashMap<String, Int>()
            data["postId"] = postId
            apiService.getPostList(data).enqueue(object : Callback<List<Comment>> {
                override fun onResponse(
                    call: Call<List<Comment>>?,
                    response: Response<List<Comment>>?
                ) {
                    comments.value = response!!.body()
                    Log.d("TUAN1", "onResponse: " + comments.value!!.size)
                }

                override fun onFailure(call: Call<List<Comment>>?, t: Throwable?) {
                    Log.d("TUAN2", "onResponse: " + t?.message)

                }

            })

        } catch (e: Exception) {
            Log.d("TUAN3", "onResponse: " + e.message)

        }
    }

    fun post(post: Post) {
        try {
            apiService.post(post).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>?, response: Response<Post>?) {
                    Log.d("TUAN1", "onResponse: " + response!!.message())
                }
                override fun onFailure(call: Call<Post>?, t: Throwable?) {
                    Log.d("TUAN2", "onResponse: " + t?.message)
                }

            })
        } catch (e: Exception) {
            Log.d("TUAN3", "onResponse: " + e.message)
        }
    }

    suspend fun insertSong(listSong: List<Song>) {
        songDao.insertSong(listSong)
    }

    suspend fun updateSongToFavourite(song: Song, isFavourite: Int) {
        songDao.updateSongToFavourite(song.id, isFavourite)
    }

    suspend fun deleteById(id: Int) {
        songDao.deleteById(id = id)
    }

    fun getFavouriteList(): LiveData<List<Song>> {
        return songDao.getPlaylist()
    }
}