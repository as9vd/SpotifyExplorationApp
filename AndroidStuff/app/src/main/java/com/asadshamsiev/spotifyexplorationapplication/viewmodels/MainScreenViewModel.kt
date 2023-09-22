package com.asadshamsiev.spotifyexplorationapplication.viewmodels

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.SimpleTrack
import com.adamratzman.spotify.models.SpotifySearchResult
import com.asadshamsiev.spotifyexplorationapplication.UNINIT_STR
import com.asadshamsiev.spotifyexplorationapplication.utils.SpotifyState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Stable
class MainScreenViewModel : ViewModel() {
    // We'll use this to tell if the local Spotify (1) thing (SpotifyAppRemote) doesn't work.
    var isLocalSpotifyDead: Boolean
        get() = _isLocalSpotifyDead.value
        private set(value) {
            _isLocalSpotifyDead.value = value
        }
    private val _isLocalSpotifyDead = mutableStateOf(false)

    // This'll be for the search stuff (2).
    var isSpotifyApiDead: Boolean
        get() = _isSpotifyApiDead.value
        private set(value) {
            _isSpotifyApiDead.value = value
        }

    private val _isSpotifyApiDead = mutableStateOf(false)

    // State to indicate if there was an error fetching tracks
    var failedToGetTracks: Boolean
        get() = _failedToGetTracks.value
        private set(value) {
            _failedToGetTracks.value = value
        }
    private val _failedToGetTracks = mutableStateOf(false)

    // If "Explore Button" clicked.
    private var _isExploreSessionStarted by mutableStateOf(false)
    val isExploreSessionStarted: Boolean get() = _isExploreSessionStarted
    fun setIsExploreSessionStarted(newConditional: Boolean) {
        _isExploreSessionStarted = newConditional
    }

    var trackUri: String
        get() = _trackUri.value
        private set(value) {
            _trackUri.value = value
        }
    private val _trackUri = mutableStateOf(UNINIT_STR)
    fun setTrackUri(trackUri: String) {
        _trackUri.value = trackUri
    }

    var trackName: String
        get() = _trackName.value
        private set(value) {
            _trackName.value = value
        }
    private val _trackName = mutableStateOf(UNINIT_STR)

    var albumUri: String
        get() = _albumUri.value
        private set(value) {
            _albumUri.value = value
        }
    private val _albumUri = mutableStateOf(UNINIT_STR)
    fun setAlbumUri(albumUri: String) {
        _albumUri.value = albumUri
    }

    var albumName: String
        get() = _albumName.value
        private set(value) {
            _albumName.value = value
        }
    private val _albumName = mutableStateOf(UNINIT_STR)

    // For changing colours.
    var colourIndex: Int
        get() = _colourIndex.value
        private set(value) {
            _colourIndex.value = value
        }
    private val _colourIndex = mutableStateOf(0)
    fun incrementColourIndex() {
        if (_colourIndex.value == 5) {
            _colourIndex.value = 0
        } else {
            _colourIndex.value += 1
        }
    }

    var combinedSpotifyState: SpotifyState
        get() = _combinedSpotifyState.value
        private set(value) {
            _combinedSpotifyState.value = value
        }
    private val _combinedSpotifyState = mutableStateOf(
        SpotifyState(
            albumName = UNINIT_STR,
            currentAlbumTracks = ArrayList()
        )
    )

    private var _currentAlbumTracks by mutableStateOf(emptyList<Pair<SimpleTrack, Pair<String, String>>>())
    val currentAlbumTracks: List<Pair<SimpleTrack, Pair<String, String>>>
        get() = _currentAlbumTracks

    fun setCurrentAlbumTracks(currentAlbumTracks: ArrayList<Pair<SimpleTrack, Pair<String, String>>>) {
        _currentAlbumTracks = currentAlbumTracks
    }

    val uniqueTracks: List<SimpleTrack> by derivedStateOf {
        _currentAlbumTracks.map { it.first }.toSet().toList()
    }

    var currentIntervalIndex: Int
        get() = _currentIntervalIndex.value
        private set(value) {
            _currentIntervalIndex.value = value
        }
    private val _currentIntervalIndex =
        mutableStateOf(0)

    suspend fun searchForResult(
        publicSpotifyAppApi: SpotifyAppApi?,
        query: String
    ): SpotifySearchResult? {
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