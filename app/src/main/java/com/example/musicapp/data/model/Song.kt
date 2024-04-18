package com.example.musicapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicapp.ui.view.PlaylistFragment

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val imageUrl: String,
    val title: String,
    val artist: String,
    val duration: String,
    val media: String,
    val isFavourite: Boolean
) {
    constructor() : this(imageUrl = "", title = "", artist = "", duration = "", media = "", isFavourite = false)
}
