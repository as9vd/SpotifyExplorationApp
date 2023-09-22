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
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

const val UNINIT_STR = ""
const val clientId = "d6d33d89d3a044618291f268d1eea409"
const val clientSecret = "58f5caf8a73b439689b108824daf4c79"
const val redirectUri = "com.asadshamsiev.spotifyexplorationapplication://callback"

class MainActivity : ComponentActivity() {
    private lateinit var mainScreenViewModel: MainScreenViewModel

    private var spotifyAppRemote: MutableState<SpotifyAppRemote?> = mutableStateOf(null)
    private var publicSpotifyAppApi: MutableState<SpotifyAppApi?> = mutableStateOf(null)

    override fun onStart() {
        super.onStart()

        // 1. Load the ViewModel.
        mainScreenViewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)

        // 2a. Set the connection parameters..
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        // 2b. Connect the Spotify connected to the local app on my phone.
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                Log.d("SpotifyStuff", "Connected! Finally.")
                spotifyAppRemote.value = appRemote

                // Listen to every single update in the PlayerState.
                // This means whenever the current track, playback speed, or pause status changes,
                // this block of code down here will run.
                spotifyAppRemote.value?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
                    // I have these booleans below so blocks of code don't redundantly get called.
                    val isSongNew: Boolean = mainScreenViewModel.trackUri.value != state.track.uri
                    val isAlbumNew: Boolean = mainScreenViewModel.albumUri.value != state.track.album.uri

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
                mainScreenViewModel.setLocalSpotifyDeadState(true)
            }
        })

        // 3. Connect the Spotify API that'll call the public search API.
        lifecycleScope.launch {
            buildSpotifyPublicApi()
        }
    }

    // Flipper is used to get more information about the application.
    // This is good for breaking down network calls (lol) and understanding crashes,
    // without having to go through 500k+ Logcat entries.
    private fun connectFlipper() {
        // I got this from the official Flipper site.
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

    // Step 3 of the onStart() (as of 9/19).
    // Attempts to set up the SpotifyApi (by this Adam Kotzman dude).
    // If it fails, MainScreenViewModel will make this known by updating isSpotifyApiDead.
    private suspend fun buildSpotifyPublicApi() {
        try {
            // Calls the builder that'll initialise the public Spotify API with the ID and secret.
            publicSpotifyAppApi.value = spotifyAppApi(clientId = clientId, clientSecret = clientSecret).build(
                enableDefaultTokenRefreshProducerIfNoneExists = true
            )
            mainScreenViewModel.setSpotifyApiDeadState(false)
        } catch (e: Exception) {
            Log.e("SpotifyApiError", "Failed to build Spotify public API.", e)
            mainScreenViewModel.setSpotifyApiDeadState(true)
        }
    }

    // If a song changes, update the current track uri and track name.
    // Gets called whenever PlayerState changes in the spotifyAppRemote,
    // which is a package maintained by the Spotify corporation itself.
    private fun handleSongChange(state: PlayerState) {
        val currentTrackUri: String = mainScreenViewModel.trackUri.value

        Log.d("TrackUri", "Current TrackUri: ${currentTrackUri}")
        Log.d("TrackUri", "State TrackUri: ${state.track.uri}")

        mainScreenViewModel.setTrackUri(state.track.uri)
        mainScreenViewModel.setTrackName(state.track.name)
    }

    private fun handleAlbumChange(state: PlayerState) {
        // Obviously, update the current album uri and name.
        mainScreenViewModel.setAlbumUri(state.track.album.uri)
        mainScreenViewModel.setAlbumName(state.track.album.name)

        lifecycleScope.launch {
            Log.d("AlbumUri", "Album changed!")
            try {
                // This is the uri of the album we've just started playing.
                val currAlbumUri: String = mainScreenViewModel.albumUri.value
                val album =
                    publicSpotifyAppApi.value?.albums?.getAlbum(currAlbumUri)
                val isValidAlbum: Boolean = (album?.tracks != null)

                if (isValidAlbum) {
                    // Originally was val updatedAlbumTracks = arrayListOf<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>()
                    val updatedAlbumTracks = ArrayList<Pair<SimpleTrack, Pair<String, String>>>()
                    val batchTracks = ArrayList<Pair<SimpleTrack, Pair<String, String>>>()
                    var counter = 0

                    for (track in album!!.tracks) {
                        val trackLength: Int = track.length

                        // TODO: Check if it's playable in bloody America. "US" in availableMarkets is too slow.
                        val isPlayable = track.availableMarkets.size > 0
                        val isOfNotableLength = trackLength > 20000

                        // If it's not playable anywhere, or it's less than 20 seconds, just skip it.
                        if (!isPlayable || !isOfNotableLength) {
                            continue
                        }

                        val segments: ArrayList<Pair<String, String>> = TrackUtils.sampleSong(trackLength)

                        // Segment is a Pair<String, String>; first is start, second is end.
                        // In time format (e.g. 1:28).
                        // for (segment in segments) {
                            // updatedAlbumTracks.add(Pair(track, segment))
                        // }

                        for (segment in segments) {
                            batchTracks.add(Pair(track, segment))
                        }

                        counter++
                        if (counter % 3 == 0) { // Do it in batches.
                            updatedAlbumTracks.addAll(batchTracks)
                            mainScreenViewModel.setCurrentAlbumTracks(ArrayList(updatedAlbumTracks))
                            batchTracks.clear()
                            delay(2000L)
                        }
                    }

                    // Might be redundant?
                    // If it's the same album, then don't update it.
                    // But we check for that in the PlayerState subscription block?
                    // I don't know why I've got this here.
                    // if (mainScreenViewModel.currentAlbumTracks.value == updatedAlbumTracks) {
                        // return@launch
                    // }

                    // mainScreenViewModel.setCurrentAlbumTracks(updatedAlbumTracks)
                    if (batchTracks.size > 0) {
                        updatedAlbumTracks.addAll(batchTracks)
                        mainScreenViewModel.setCurrentAlbumTracks(ArrayList(updatedAlbumTracks))
                    }

                    mainScreenViewModel.setCombinedSpotifyState(
                        SpotifyState(state.track.album.name, updatedAlbumTracks)
                    )
                    mainScreenViewModel.setFailedToGetTracks(false)
                } else {
                    // If the album's tracks is null, then the album is screwed.
                    mainScreenViewModel.setFailedToGetTracks(true)
                }
            } catch (e: Exception) {
                Log.d(
                    "CurrentAlbumTracks",
                    "Failed to get album tracks: ${e}"
                )
                mainScreenViewModel.setFailedToGetTracks(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Connect flipper.
        connectFlipper()

        // 2. Populate the Compose stuff.
        setContent {
            val colourIndex: Int = mainScreenViewModel.colourIndex.collectAsState().value
            val spotifyAppRemote = spotifyAppRemote.value
            val publicSpotifyAppApi = publicSpotifyAppApi.value

            AppTheme(colourIndex = colourIndex) {
                MainScreen(
                    viewModel = mainScreenViewModel,
                    spotifyAppRemote = spotifyAppRemote,
                    publicSpotifyAppApi = publicSpotifyAppApi
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Release the local Spotify API, if it's been properly initialised.
        if (spotifyAppRemote.value != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote.value)
        }

        // TODO: Maybe the same for the public Spotify API?
    }
}