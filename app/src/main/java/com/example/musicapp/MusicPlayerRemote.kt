package com.example.musicapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.musicapp.data.model.Song
import com.example.musicapp.service.MediaPlaybackService
import java.util.WeakHashMap

object MusicPlayerRemote {
    var playerService: MediaPlaybackService? = null
    private val mConnectionMap = WeakHashMap<Context, ServiceBinder>()

    fun sendAllSong(songList: List<Song>, currentPosition: Int) {
        if (playerService != null) {
            playerService?.getAllSongs(songList, currentPosition)
        }
    }

    fun bindToService(context: Context, callback: ServiceConnection) {

        var realActivity: Activity? = (context as Activity).parent
        if (realActivity == null) {
            realActivity = context
        }

        val contextWrapper = ContextWrapper(realActivity)
        val intent = Intent(contextWrapper, MediaPlaybackService::class.java)
        try {
            contextWrapper.startService(intent)
        } catch (ignored: IllegalStateException) {
            ContextCompat.startForegroundService(context, intent)
        }
        Log.d("ServiceBinder", "onServiceConnected1: ")
        val binder = ServiceBinder(callback)

        Log.d("ServiceBinder", "onServiceConnected2: ")

        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MediaPlaybackService::class.java),
                binder,
                Context.BIND_AUTO_CREATE
            )
        ) {
            mConnectionMap[contextWrapper] = binder
        }
    }

    class ServiceBinder internal constructor(private val mCallback: ServiceConnection?) :
        ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaPlaybackService.LocalBinder
            Log.d("ServiceBinder", "onServiceConnected: ")
            playerService = binder.getService()
            mCallback?.onServiceConnected(className, service)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mCallback?.onServiceDisconnected(className)
            playerService = null
            Log.d("ServiceBinder", "onServiceDisconnected: ")

        }

    }

    class ServiceToken internal constructor(internal var mWrappedContext: ContextWrapper)
}