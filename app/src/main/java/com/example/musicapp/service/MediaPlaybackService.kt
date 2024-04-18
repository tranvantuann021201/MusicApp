package com.example.musicapp.service

import android.app.Notification.Action
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.media.AudioManagerCompat.requestAudioFocus
import com.example.musicapp.Constants.POSITION_KEY
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import java.io.IOException
import java.time.Duration
import kotlin.math.log

const val ACTION_PREVIOUS = "action previous"
const val ACTION_PLAY_PAUSE = "action play pause"
const val ACTION_NEXT = "action next"
const val ACTION_MAIN = "action main"
class MediaPlaybackService : Service(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener {
    var mediaPlayer: MediaPlayer? = null
    private var oldUrl = ""
    private var length = 0
    private var position = -1
    private var originalSongList: List<Song> = ArrayList()
    private var listOfAllSong: List<Song> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences
    var currentSong: Song? = null
    private lateinit var mediaSessionCompat: MediaSessionCompat

    companion object {
        private const val MEDIA_SESSION_ACTIONS = (PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_SEEK_TO)
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Lấy URL hoặc đường dẫn tới tệp âm thanh từ intent (hoặc từ nguồn khác)
        val lstMusic = intent?.getStringExtra("lstMusic")
//        if (mediaPlayer!!.isPlaying) {
//            if (url == oldUrl) {
//                mediaPlayer!!.pause()
//                length = mediaPlayer!!.currentPosition;
//            } else {
//                mediaPlayer!!.stop()
//                mediaPlayer!!.reset()
//            }
//        } else {
//            if (url == oldUrl) {
//                mediaPlayer!!.seekTo(length);
//                mediaPlayer!!.start()
//            }
//        }
//
//        if (url != oldUrl) {
//            // Đặt nguồn dữ liệu cho MediaPlayer
//            mediaPlayer?.setDataSource(url)
//            // Chuẩn bị và phát nhạc
//            mediaPlayer?.prepare()
//            mediaPlayer?.start()
//        }
//
//        oldUrl = url!!
        if (intent != null && intent.action != null) {
            when (intent.action) {
                ACTION_PREVIOUS -> {
                }

                ACTION_PLAY_PAUSE -> {
                    if (!isPlaying()) stopForeground(false)
                }

                ACTION_NEXT -> {
                }

                else -> Unit
            }
        }
        return START_NOT_STICKY
    }

    private fun initMediaPlayer(position: Int) {
        if (position != -1)
            this.currentSong = listOfAllSong[position]
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnSeekCompleteListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)

        try {
            mediaPlayer?.setDataSource(currentSong?.media)
            mediaPlayer?.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        setMediaSessionAction()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setMediaSessionMetaData()
        }

    }

    fun isPlaying(): Boolean {
        mediaPlayer?.let {
            return it.isPlaying
        }
        return false
    }

    private fun play() {
//        mediaPlayer?.let {
//            if (!requestAudioFocus()) stopSelf()
//            it.start()
//            notifyPlayPauseStateChanged()
//            startForegroundService()
//        }
    }


    fun seekTo(seekPosition: Int) {
        mediaPlayer?.seekTo(seekPosition)
        setMediaSessionAction()
    }

    fun getCurrentPosition(): Int {
        mediaPlayer?.let {
            return it.currentPosition
        }
        return 0
    }

    fun initMediaPlayer(songPath: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnSeekCompleteListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
        try {
            mediaPlayer?.setDataSource(songPath)
            mediaPlayer?.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        setMediaSessionAction()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setMediaSessionMetaData()
        }
    }

    fun setMediaSessionAction() {
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(MEDIA_SESSION_ACTIONS)
            .setState(
                if (isPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                getCurrentPosition().toLong(), 1f
            )

        mediaSessionCompat.setPlaybackState(stateBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMediaSessionMetaData() {
        val song = currentSong
        if (song == null) {
            mediaSessionCompat.setMetadata(null)
            return
        }

        val metadata = MediaMetadataCompat.Builder().apply {
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.title)
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
            val components = song.duration
            val minutes = components[0].toLong()
            val seconds = components[1].toLong()
            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (minutes * 60 + seconds))
        }
        mediaSessionCompat.setMetadata(metadata.build())
    }

    fun getAllSongs(songList: List<Song>?, clickedPosition: Int) {
        if (!songList.isNullOrEmpty() && clickedPosition < songList.size && clickedPosition >= 0) {
            this.position = clickedPosition
            originalSongList = ArrayList(songList)
            this.listOfAllSong = ArrayList(originalSongList)
            initMediaPlayer(position)
//            notifyCurrentSongChanged()
        } else if (!songList.isNullOrEmpty()) {
            originalSongList = ArrayList(songList)
            this.listOfAllSong = ArrayList(originalSongList)
        } else {
            this.listOfAllSong = emptyList()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onPrepared(mp: MediaPlayer?) {
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
    }

    override fun onCompletion(mp: MediaPlayer?) {
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mediaPlayer!!.stop()
        Toast.makeText(this, "Invalid format or song", Toast.LENGTH_SHORT).show()
        with(sharedPreferences.edit()) {
            putInt(POSITION_KEY, position)
            apply()
        }
        stopForeground(false)
        return false
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlaybackService {
            return this@MediaPlaybackService
        }
    }
}