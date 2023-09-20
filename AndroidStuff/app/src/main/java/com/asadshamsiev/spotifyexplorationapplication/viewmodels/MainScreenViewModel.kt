package com.asadshamsiev.spotifyexplorationapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.SimpleTrack
import com.adamratzman.spotify.models.SpotifySearchResult
import com.asadshamsiev.spotifyexplorationapplication.UNINIT_STR
import com.asadshamsiev.spotifyexplorationapplication.utils.SpotifyState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

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

    val currentAlbumTracks: StateFlow<ArrayList<Pair<SimpleTrack, Pair<String, String>>>>
        get() = _currentAlbumTracks
    private val _currentAlbumTracks =
        MutableStateFlow(ArrayList<Pair<SimpleTrack, Pair<String, String>>>())
    fun setCurrentAlbumTracks(currentAlbumTracks: ArrayList<Pair<SimpleTrack, Pair<String, String>>>) {
        _currentAlbumTracks.value = currentAlbumTracks
    }

    suspend fun searchForResult(publicSpotifyAppApi: SpotifyAppApi?, query: String): SpotifySearchResult? {
        var res: SpotifySearchResult? = null

        if (publicSpotifyAppApi != null) {
            // Otherwise, might block the main thread.
            try {
                res = withContext(Dispatchers.IO) {
                    publicSpotifyAppApi.search.search(
                        query = query,
                        searchTypes = listOf(SearchApi.SearchType.Album).toTypedArray(),
                        limit = 4
                    )
                }
            } catch (e: Exception) {
                Log.d("searchForResult", "searchForResult failed: $e")
            }
        }

        return res
    }
}