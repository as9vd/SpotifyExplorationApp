package com.asadshamsiev.spotifyexplorationapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.SimpleTrack
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.spotifyAppApi
import com.asadshamsiev.spotifyexplorationapplication.ui.theme.AppTheme
import com.asadshamsiev.spotifyexplorationapplication.utils.SpotifyState
import com.asadshamsiev.spotifyexplorationapplication.utils.TrackUtils
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val UNINIT_STR = ""
const val clientId = "d6d33d89d3a044618291f268d1eea409"
const val clientSecret = "58f5caf8a73b439689b108824daf4c79"
const val redirectUri = "com.asadshamsiev.spotifyexplorationapplication://callback"

class MainActivity : ComponentActivity() {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var publicSpotifyAppApi: SpotifyAppApi? = null

    private lateinit var viewModel: MainScreenViewModel

    override fun onStart() {
        super.onStart()

        // 0. Set the connection parameters..
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        // 1. Connect the Spotify connected to the local app on my phone.
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                Log.d("SpotifyStuff", "Connected! Finally.")
                spotifyAppRemote = appRemote
                spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
                    val isSongNew: Boolean = viewModel.trackUri.value != state.track.uri
                    val isAlbumNew = viewModel.albumUri.value != state.track.album.uri

                    if (isSongNew) {
                        run { // 1. Change song state (if changed).
                            handleSongChange(state)
                        }
                    }

                    if (isAlbumNew) {
                        run {
                            handleAlbumChange(state) // 2. Change album state (if changed).
                        }
                    }
                }
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyStuff", throwable.message, throwable)
                viewModel.setLocalSpotifyDeadState(true)
            }
        })

        // 2. Connect the Spotify API that'll call the public search shit.
        lifecycleScope.launch {
            buildSpotifyPublicApi()
        }
    }

    private suspend fun buildSpotifyPublicApi() {
        try {
            publicSpotifyAppApi =
                spotifyAppApi(clientId = clientId, clientSecret = clientSecret).build(
                    enableDefaultTokenRefreshProducerIfNoneExists = true
                )
        } catch (e: Exception) {
            Log.e("SpotifyApiError", "Failed to build Spotify public API.", e)
            viewModel.setSpotifyApiDeadState(true)
        }
    }

    private fun handleSongChange(state: PlayerState) {
        val currentTrackUri: String = viewModel.trackUri.value

        Log.d("TrackUri", "Current TrackUri: ${currentTrackUri}")
        Log.d("TrackUri", "State TrackUri: ${state.track.uri}")

        viewModel.setTrackUri(state.track.uri)
        viewModel.setTrackName(state.track.name)
    }

    private fun handleAlbumChange(state: PlayerState) {
        viewModel.setAlbumUri(state.track.album.uri)
        viewModel.setAlbumName(state.track.album.name)

        lifecycleScope.launch {
            Log.d("AlbumUri", "Album changed!")
            try {
                val currAlbumUri: String = viewModel.albumUri.value
                val album =
                    publicSpotifyAppApi?.albums?.getAlbum(currAlbumUri)
                val isValidAlbum: Boolean = (album?.tracks != null)

                if (isValidAlbum) {
                    val updatedAlbumTracks =
                        arrayListOf<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>()

                    for (track in album!!.tracks) {
                        val trackLength: Int = track.length
                        updatedAlbumTracks.add(
                            Pair(
                                TrackUtils.sampleSong(
                                    trackLength
                                ), track
                            )
                        )
                    }

                    // Might be redundant?
                    if (viewModel.currentAlbumTracks.value == updatedAlbumTracks) {
                        return@launch
                    }

                    viewModel.setCurrentAlbumTracks(updatedAlbumTracks)
                    viewModel.setCombinedSpotifyState(
                        SpotifyState(
                            state.track.album.name,
                            updatedAlbumTracks
                        )
                    )
                    viewModel.setFailedToGetTracks(false)
                }
            } catch (e: Exception) {
                Log.d(
                    "CurrentAlbumTracks",
                    "Failed to get album tracks: ${e}"
                )
                viewModel.setFailedToGetTracks(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Connect flipper.
        connectFlipper()

        // 2. Load the ViewModel.
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)

        // 3. Populate the Compose stuff.
        setContent {
            AppTheme(colourIndex = viewModel.colourIndex.value) {
                MainScreen(
                    viewModel = viewModel,
                    spotifyAppRemote = spotifyAppRemote,
                    publicSpotifyAppApi = publicSpotifyAppApi
                )
            }
        }
    }

    private fun connectFlipper() {
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            SoLoader.init(this, false)
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(
                InspectorFlipperPlugin(
                    applicationContext,
                    DescriptorMapping.withDefaults()
                )
            )
            client.start()
        }
    }
}