package com.example.musicapp.util

import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import com.example.musicapp.MusicPlayerRemote
import com.example.musicapp.data.model.Song

object PlayerHelper {
    fun getCurrentSong(sharedPreferences: SharedPreferences): Song? {
        return if (MusicPlayerRemote.playerService?.mediaPlayer != null && MusicPlayerRemote.playerService?.currentSong != null) {
            MusicPlayerRemote.playerService?.currentSong
        } else {
            SharedPreferenceUtil.getCurrentSong(sharedPreferences)
        }
    }

    fun getSongThumbnail(songPath: String): ByteArray? {
        var imgByte: ByteArray?
        MediaMetadataRetriever().also {
            try {
                it.setDataSource(songPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imgByte = it.embeddedPicture
            it.release()
        }
        return imgByte
    }
}