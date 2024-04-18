package com.example.musicapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.data.model.Post
import com.example.musicapp.data.model.Song
import com.example.musicapp.data.model.songsList
import com.example.musicapp.data.repostirory.SongRepository

class PlaylistViewModel(application: Application) : ViewModel() {
    private val _songList = MutableLiveData<List<Song>>()
    val songList: LiveData<List<Song>> = _songList
    private val songRepository = SongRepository(application = application)

    suspend fun insertAll(songList: List<Song>) {
        songRepository.insertSong(songList)
    }

    fun requestCommentAPI(postId: Int) {
        songRepository.getListComment(postId)
    }

    fun requestPostAPI(post: Post) {
        songRepository.post(post)
    }

    suspend fun updateSongToFavourite(song: Song) {
        songRepository.updateSongToFavourite(song, isFavourite = if (!song.isFavourite) 1 else 0)
    }

    suspend fun deleteFavouriteSong(song: Song) {
        songRepository.deleteById(song.id)
    }

    fun getFavouriteList() : LiveData<List<Song>>{
        return songRepository.getFavouriteList()
    }

    fun getPlaylist() {
        _songList.value = songsList
    }

    class PlaylistViewModelFactory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlaylistViewModel(application) as T
            }
            throw IllegalAccessException("Unable construct viewModel")
        }
    }
}