package com.example.musicapp.ui.adpater

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.MainActivity
import com.example.musicapp.MusicPlayerRemote
import com.example.musicapp.R
import com.example.musicapp.databinding.ItemMusicsBinding
import com.example.musicapp.data.model.Song
import com.example.musicapp.service.MediaPlaybackService
import com.example.musicapp.ui.view.PlaylistFragment

class MusicsAdapter(private val context: Context, private val dataset: List<Song>) :
    RecyclerView.Adapter<MusicsAdapter.ItemViewHolder>() {
    private lateinit var listener: PlaylistFragment.onClickFavourite
    fun setOnClickFavouriteSong(listeners: PlaylistFragment.onClickFavourite) {
        listener = listeners
    }

    class ItemViewHolder(
        private val itemBinding: ItemMusicsBinding,
        private val context: Context,
        private val listeners: PlaylistFragment.onClickFavourite
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @OptIn(UnstableApi::class)
        fun bind(songs: List<Song>, position: Int) {
            val song = songs[position]
            if (song.isFavourite) {
                itemBinding.icUnfavourite.visibility = View.GONE
                itemBinding.icFavourite.visibility = View.VISIBLE
            } else {
                itemBinding.icFavourite.visibility = View.GONE
                itemBinding.icUnfavourite.visibility = View.VISIBLE
            }

            itemBinding.tvTitle.text = song.title
            itemBinding.tvSinger.text = song.artist
            Glide.with(context)
                .load(song.imageUrl)
                .override(60, 60)
                .fitCenter()
                .into(itemBinding.imgTrackList)

            itemBinding.root.setOnClickListener {
                val intent = Intent(context, MediaPlaybackService::class.java)
                intent.putExtra("lstMusic", songs as ArrayList<Song>)
                context.startService(intent)
//                MusicPlayerRemote.sendAllSong(songs, position)

            }

            itemBinding.icUnfavourite.setOnClickListener {
                listeners.onClickFavourite(song)
            }

            itemBinding.icFavourite.setOnClickListener {
                listeners.onClickFavourite(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val song = ItemMusicsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(song, context, listener)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataset, position)
    }
}