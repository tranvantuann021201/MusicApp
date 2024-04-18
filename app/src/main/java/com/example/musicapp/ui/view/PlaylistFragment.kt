package com.example.musicapp.ui.view

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.MainActivity
import com.example.musicapp.MusicPlayerRemote
import com.example.musicapp.R
import com.example.musicapp.data.model.Post
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.FragmentPlaylistBinding
import com.example.musicapp.ui.adpater.MusicsAdapter
import com.example.musicapp.ui.viewmodel.PlaylistViewModel
import kotlinx.coroutines.launch
import java.net.URL

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var viewModel: PlaylistViewModel
    private lateinit var viewModelFactory: PlaylistViewModel.PlaylistViewModelFactory
    private lateinit var adapter: MusicsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        viewModelFactory = PlaylistViewModel.PlaylistViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[PlaylistViewModel::class.java]

//        viewModel.getPlaylist()
//        viewModel.songList.observe(viewLifecycleOwner) { it ->
//            lifecycleScope.launch {
//                viewModel.insertAll(it)
//            }
//        }

        viewModel.getFavouriteList().observe(viewLifecycleOwner) { playList ->
            adapter = MusicsAdapter(requireContext(), playList)
            binding.rvMusics.adapter = adapter
            binding.rvMusics.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter.setOnClickFavouriteSong(object : onClickFavourite {
                @OptIn(UnstableApi::class)
                override fun onClickFavourite(song: Song) {
                    lifecycleScope.launch {
                        viewModel.updateSongToFavourite(song)
                    }
//                    viewModel.requestCommentAPI(1)
                    viewModel.requestPostAPI(Post("Tuan", "TranVanTuan", 1))
                    val intent = Intent(requireContext(), MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                }

            })
        }

        MusicPlayerRemote.bindToService(requireContext(), object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                if (MusicPlayerRemote.playerService?.mediaPlayer == null) {
//                    MusicPlayerRemote.playerService?.initMediaPlayer(currentSong!!.path)
                    viewModel.getFavouriteList().observe(viewLifecycleOwner) {
                        if (it.isNotEmpty()) {
                            MusicPlayerRemote.sendAllSong(it as MutableList<Song>, -1)
                        } else {
                            MusicPlayerRemote.sendAllSong(
                                emptyList<Song>() as MutableList<Song>,
                                -1
                            )
                        }
                    }
                }

            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMusics.setHasFixedSize(true)
        binding.rvMusics.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

//    override fun onResume() {
//        super.onResume()
//        APICallback().execute()
//    }

//    private inner class APICallback : AsyncTask<Void, Void, String>() {
//        override fun doInBackground(vararg params: Void?): String {
//            try {
//                val client = OkHttpClient()
//
//                val request = Request.Builder()
//                    .url("https://soundcloud-scraper.p.rapidapi.com/v1/track/metadata?track=https%3A%2F%2Fsoundcloud.com%2Fedsheeran%2Fphotograph")
//                    .get()
//                    .addHeader("X-RapidAPI-Key", "6905191d7emsh2f44223a0062736p172c63jsn84eedbfaeba3")
//                    .addHeader("X-RapidAPI-Host", "soundcloud-scraper.p.rapidapi.com")
//                    .build()
//
//                val response = client.newCall(request).execute()
//                return response.body?.string()!!
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            return null.toString()
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            // Xử lý kết quả ở đây
//            result?.let {
//                Log.d("API_RESULT", "onPostExecute: " + result)
//            }
//        }
//
//    }

    interface onClickFavourite {
        fun onClickFavourite(song: Song)
    }

}