package com.example.musicapp.service

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat

class MediaSessionCallback(private val context: Context, private val service: MediaPlaybackService) :
    MediaSessionCompat.Callback() {
    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        service.seekTo(pos.toInt())
    }
}