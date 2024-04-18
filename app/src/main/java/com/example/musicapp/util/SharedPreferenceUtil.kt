package com.example.musicapp.util

import android.content.SharedPreferences
import com.example.musicapp.Constants
import com.example.musicapp.Constants.SAVE_CURRENT_SONG_KEY
import com.example.musicapp.data.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object SharedPreferenceUtil {

    fun saveCurrentSong(currentSong: Song, sharedPreferences: SharedPreferences) {
        Gson().apply {
            val songJson = toJson(currentSong)
            with(sharedPreferences.edit()) {
                putString(SAVE_CURRENT_SONG_KEY, songJson)
                apply()
            }
        }
    }

    fun getCurrentSong(sharedPreferences: SharedPreferences): Song? {
        val songJson = sharedPreferences.getString(SAVE_CURRENT_SONG_KEY, null)
        val type = object : TypeToken<Song?>() {}.type
        Gson().apply {
            return fromJson(songJson, type)
        }
    }

    fun saveCurrentPosition(sharedPreferences: SharedPreferences, currentPosition: Int) {
        with(sharedPreferences.edit()) {
            putInt(Constants.CURRENT_SONG_DURATION_KEY, currentPosition)
            apply()
        }
    }

    fun getPosition(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(Constants.POSITION_KEY, 0)
    }
}