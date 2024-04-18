package com.example.musicapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.data.model.Song
import com.example.musicapp.ui.view.PlaylistFragment

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(listSong: List<Song>)

    @Query("UPDATE songs SET isFavourite = :isFavourite WHERE id = :id")
    suspend fun updateSongToFavourite(id: Int, isFavourite: Int)

    @Query("SELECT * FROM songs")
    fun getPlaylist(): LiveData<List<Song>>

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteById(id: Int)
}