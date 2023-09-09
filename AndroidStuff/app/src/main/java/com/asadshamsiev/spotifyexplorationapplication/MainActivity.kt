package com.asadshamsiev.spotifyexplorationapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.SimpleTrack
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.asadshamsiev.spotifyexplorationapplication.ui.theme.AppTheme
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val UNINIT_STR = ""
const val clientId = "d6d33d89d3a044618291f268d1eea409"
const val clientSecret = "58f5caf8a73b439689b108824daf4c79"
const val redirectUri = "com.asadshamsiev.spotifyexplorationapplication://callback"

class MainActivity : ComponentActivity() {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var publicSpotifyAppApi: SpotifyAppApi? = null

    // We'll use this to tell if the local Spotify (1) thing (SpotifyAppRemote) doesn't work.
    private var localSpotifyDead = mutableStateOf(false)

    // This'll be for the search stuff (2).
    private var spotifyApiDead = mutableStateOf(false)

    private var failedToGetTracks = mutableStateOf(false)

    // This is what is used to check if music is currently playing, and if the track list should be shown.
    private var musicPlaying = mutableStateOf(false)

    private val trackUri = mutableStateOf(UNINIT_STR)
    private val trackName = mutableStateOf(UNINIT_STR)

    private val albumUri = mutableStateOf(UNINIT_STR)
    private val albumName = mutableStateOf(UNINIT_STR)

    @SuppressLint("MutableCollectionMutableState")
    private val currentAlbumTracks = mutableStateOf(arrayListOf<Any>())

    override fun onStart() {
        super.onStart()

        // Set the connection parameters.
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
                    run {
                        musicPlaying.value = !state.isPaused

                        trackUri.value = state.track.uri
                        trackName.value = state.track.name

                        albumUri.value = state.track.album.uri
                        albumName.value = state.track.album.name
                    }
                }
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyStuff", throwable.message, throwable)
                localSpotifyDead.value = true
            }
        })

        // 2. Connect the Spotify API that'll call the public search shit.
        lifecycleScope.launch {
            buildSpotifyPublicApi()
        }

        lifecycleScope.launch {
            snapshotFlow { albumUri.value }.distinctUntilChanged().collect {
                Log.d("Name Changed", "NAME CHANGED YOU PAGAN!")
                try {
                    val album = publicSpotifyAppApi?.albums?.getAlbum(albumUri.value)
                    if (album?.tracks != null) {
                        val updatedAlbumTracks = arrayListOf<Any>()
                        updatedAlbumTracks.addAll(listOf(album.tracks))
                        currentAlbumTracks.value = updatedAlbumTracks
                        failedToGetTracks.value = false
                    }
                } catch (e: Exception) {
                    Log.d("Error", "Failed to get album tracks.")
                    failedToGetTracks.value = true
                }
            }
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
            spotifyApiDead.value = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Connect flipper.
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

        setContent {
            AppTheme {
                MainScreen(
                    spotifyApiDead.value,
                    localSpotifyDead.value,
                    "Track: ${trackName.value}",
                    "Album: ${albumName.value}",
                    currentAlbumTracks.value
                )
            }
        }
    }

    private suspend fun searchForResult(query: String): SpotifySearchResult? {
        var res: SpotifySearchResult? = null

        if (publicSpotifyAppApi != null) {
            // Otherwise, might block the main thread.
            res = withContext(Dispatchers.IO) {
                publicSpotifyAppApi!!.search.search(
                    query = query,
                    searchTypes = listOf(SearchApi.SearchType.Album).toTypedArray()
                )
            }
        }

        return res
    }

    @Composable
    fun SearchConditionalErrors(spotifyApiDead: Boolean, localSpotifyDead: Boolean) {
        if (spotifyApiDead) {
            Text("Spotify cannot authenticate your account.")
        } else if (localSpotifyDead) {
            Text("You haven't got Spotify installed on your phone.")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBox(textFieldQuery: MutableState<String>) {
        Text("Start a Session 🐽☃\uFE0F")
        TextField(
            value = textFieldQuery.value,
            placeholder = {
                Text("Click to start typing!")
            },
            onValueChange = {
                textFieldQuery.value = it
            }
        )
    }

    @Composable
    fun AlbumCardResults(
        textFieldQuery: MutableState<String>,
        isLoading: MutableState<Boolean>,
        foundStuff: MutableList<List<String>>
    ) {
        when {
            textFieldQuery.value.isEmpty() -> {
                Text("Type something.") // When nothing's been typed yet.
            }

            isLoading.value -> {
                CircularProgressIndicator() // Show that it's visibly fetching results
            }

            foundStuff.isNotEmpty() -> {
                // Else, show the result.
                for (infoTuple in foundStuff) {
                    val (artistName, albumName, link, uri) = infoTuple

                    AlbumCard(
                        artistName = artistName,
                        albumName = albumName,
                        link = link,
                        modifier = Modifier.clickable {
                            spotifyAppRemote?.playerApi?.play(uri)
                        }
                    )
                }
            }

            else -> {
                Text("No results found.") // Terrible search.
            }
        }
    }

    @Composable
    fun MainScreen(
        spotifyApiDead: Boolean,
        localSpotifyDead: Boolean,
        currTrackName: String,
        currAlbumName: String,
        currentAlbumTracks: MutableList<Any>
    ) {
        val textFieldQuery = remember { mutableStateOf(UNINIT_STR) }
        val foundStuff = remember { mutableListOf<List<String>>() }
        val isLoading = remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        // Whenever the query gets updated.
        LaunchedEffect(textFieldQuery.value) {
            if (textFieldQuery.value.isNotEmpty()) {
                // In order to show a loading Composable whilst results are fetched.
                isLoading.value = true

                delay(1000L) // Debounce for 1 second.
                val result = searchForResult(textFieldQuery.value)
                foundStuff.clear()

                // Just for testing.. teehee!
                if (result?.albums != null && result.albums?.size!! > 0) {
                    val albumsList: ArrayList<List<String>> = arrayListOf()

                    for (i in 0 until minOf(3, result.albums!!.size)) {
                        val album = result.albums!![i]
                        val artistName = album.artists[0].name
                        val albumName = album.name
                        val image = album.images[0].url
                        val uri = album.uri.uri

                        // .uri is good. Returns AlbumUri/SpotifyUri.
                        albumsList.add(listOf(artistName, albumName, image, uri))
                    }

                    foundStuff.addAll(albumsList)
                }

                isLoading.value = false
            }
        }

        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // These errors only show when the 1. local phone API is dead or 2. the public API is dead.
            SearchConditionalErrors(
                spotifyApiDead = spotifyApiDead,
                localSpotifyDead = localSpotifyDead
            )

            // This is the search box and the albums it returns.
            SearchBox(textFieldQuery = textFieldQuery)
            AlbumCardResults(
                textFieldQuery = textFieldQuery,
                isLoading = isLoading,
                foundStuff = foundStuff
            )

            // This will show the track list of the album of the currently played song.
            TrackListCards(
                spotifyAppRemote = spotifyAppRemote,
                currTrackName = currTrackName,
                currAlbumName = currAlbumName,
                currentAlbumTracks = currentAlbumTracks
            )
        }
    }
}