package com.asadshamsiev.spotifyexplorationapplication.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.SimpleTrack
import com.asadshamsiev.spotifyexplorationapplication.UNINIT_STR
import com.asadshamsiev.spotifyexplorationapplication.utils.SpotifyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainScreenViewModel : ViewModel() {
    // We'll use this to tell if the local Spotify (1) thing (SpotifyAppRemote) doesn't work.
    val isLocalSpotifyDead: StateFlow<Boolean> get() = _isLocalSpotifyDead
    private val _isLocalSpotifyDead = MutableStateFlow(false)
    fun setLocalSpotifyDeadState(isDead: Boolean) {
        _isLocalSpotifyDead.value = isDead
    }

    // This'll be for the search stuff (2).
    val isSpotifyApiDead: StateFlow<Boolean> get() = _isSpotifyApiDead
    private val _isSpotifyApiDead = MutableStateFlow(false)
    fun setSpotifyApiDeadState(isDead: Boolean) {
        _isSpotifyApiDead.value = isDead
    }

    // State to indicate if there was an error fetching tracks
    val failedToGetTracks: StateFlow<Boolean> get() = _failedToGetTracks
    private val _failedToGetTracks = MutableStateFlow(false)
    fun setFailedToGetTracks(failedToGetTracks: Boolean) {
        _failedToGetTracks.value = failedToGetTracks
    }

    val trackUri: StateFlow<String> get() = _trackUri
    private val _trackUri = MutableStateFlow(UNINIT_STR)
    fun setTrackUri(trackUri: String) {
        _trackUri.value = trackUri
    }

    val trackName: StateFlow<String> get() = _trackName
    private val _trackName = MutableStateFlow(UNINIT_STR)
    fun setTrackName(trackName: String) {
        _trackName.value = trackName
    }

    val albumUri: StateFlow<String> get() = _albumUri
    private val _albumUri = MutableStateFlow(UNINIT_STR)
    fun setAlbumUri(albumUri: String) {
        _albumUri.value = albumUri
    }

    val albumName: StateFlow<String> get() = _albumName
    private val _albumName = MutableStateFlow(UNINIT_STR)
    fun setAlbumName(albumName: String) {
        _albumName.value = albumName
    }

    // For changing colours.
    val colourIndex: StateFlow<Int> get() = _colourIndex
    private val _colourIndex = MutableStateFlow(0)
    fun incrementColourIndex() {
        if (_colourIndex.value == 5) {
            _colourIndex.value = 0
        } else {
            _colourIndex.value += 1
        }
    }

    val combinedSpotifyState: StateFlow<SpotifyState> get() = _combinedSpotifyState
    private val _combinedSpotifyState = MutableStateFlow(
        SpotifyState(
            albumName = UNINIT_STR,
            currentAlbumTracks = ArrayList()
        )
    )
    fun setCombinedSpotifyState(combinedSpotifyState: SpotifyState) {
        _combinedSpotifyState.value = combinedSpotifyState
    }

    val currentAlbumTracks: StateFlow<ArrayList<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>>
        get() = _currentAlbumTracks
    private val _currentAlbumTracks =
        MutableStateFlow(arrayListOf<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>())
    fun setCurrentAlbumTracks(currentAlbumTracks: ArrayList<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>) {
        _currentAlbumTracks.value = currentAlbumTracks
    }

}