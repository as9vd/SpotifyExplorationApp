package com.asadshamsiev.spotifyexplorationapplication.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
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
import com.asadshamsiev.spotifyexplorationapplication.utils.SimpleTrackWrapper
import com.asadshamsiev.spotifyexplorationapplication.utils.SpotifyState
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.immutableListOf

@Stable
class MainScreenViewModel : ViewModel() {
    var publicSpotifyAppApi: SpotifyAppApi?
        get() = _publicSpotifyAppApi.value
        set(value) {
            _publicSpotifyAppApi.value = value
        }
    private val _publicSpotifyAppApi = mutableStateOf<SpotifyAppApi?>(null)

    var spotifyAppRemote: SpotifyAppRemote?
        get() = _spotifyAppRemote.value
        set(value) {
            _spotifyAppRemote.value = value
        }
    private val _spotifyAppRemote = mutableStateOf<SpotifyAppRemote?>(null)

    // We'll use this to tell if the local Spotify (1) thing (SpotifyAppRemote) doesn't work.
    var isLocalSpotifyDead: Boolean
        get() = _isLocalSpotifyDead.value
        set(value) {
            _isLocalSpotifyDead.value = value
        }
    private val _isLocalSpotifyDead = mutableStateOf(false)

    // This'll be for the search stuff (2).
    var isSpotifyApiDead: Boolean
        get() = _isSpotifyApiDead.value
        set(value) {
            _isSpotifyApiDead.value = value
        }

    private val _isSpotifyApiDead = mutableStateOf(false)

    // State to indicate if there was an error fetching tracks
    var failedToGetTracks: Boolean
        get() = _failedToGetTracks.value
        set(value) {
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
        set(value) {
            _trackUri.value = value
        }
    private val _trackUri = mutableStateOf(UNINIT_STR)

    var trackName: String
        get() = _trackName.value
        set(value) {
            _trackName.value = value
        }
    private val _trackName = mutableStateOf(UNINIT_STR)

    var albumUri: String
        get() = _albumUri.value
        set(value) {
            _albumUri.value = value
        }
    private val _albumUri = mutableStateOf(UNINIT_STR)

    var albumName: String
        get() = _albumName.value
        set(value) {
            _albumName.value = value
        }
    private val _albumName = mutableStateOf(UNINIT_STR)

    // For changing colours.
    var colourIndex: Int
        get() = _colourIndex.value
        set(value) {
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
        set(value) {
            _combinedSpotifyState.value = value
        }
    private val _combinedSpotifyState = mutableStateOf(
        SpotifyState(
            albumName = UNINIT_STR,
            currentAlbumTracks = ArrayList()
        )
    )

    private var _currentAlbumTracks by mutableStateOf(listOf<Pair<SimpleTrackWrapper, Pair<String, String>>>())
    val currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>
         get() = _currentAlbumTracks

    fun setCurrentAlbumTracks(currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>) {
        _currentAlbumTracks = currentAlbumTracks
        uniqueTracks =_currentAlbumTracks.map { it.first }.toSet().toList()
    }

    var uniqueTracks: List<SimpleTrackWrapper>? = null

    var currentIntervalIndex: MutableState<Int>
        get() = _currentIntervalIndex
        set(value) {
            _currentIntervalIndex = value
        }
    private var _currentIntervalIndex =
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