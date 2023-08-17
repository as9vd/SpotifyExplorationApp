package com.asadshamsiev.spotifyexplorationapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.spotifyAppApi
import com.asadshamsiev.spotifyexplorationapplication.ui.theme.AppTheme
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val clientId = "d6d33d89d3a044618291f268d1eea409"
    private val clientSecret = "58f5caf8a73b439689b108824daf4c79"
    private val redirectUri = "com.asadshamsiev.spotifyexplorationapplication://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private lateinit var publicSpotifyAppApi: SpotifyAppApi

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
                spotifyAppRemote = appRemote
                Log.d("SpotifyStuff", "Connected! Finally.")
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyStuff", throwable.message, throwable)
            }
        })

        // 2. Connect the Spotify API that'll call the public search shit.
        lifecycleScope.launch {
            buildSpotifyPublicApi()
        }
    }

    private suspend fun buildSpotifyPublicApi() {
        publicSpotifyAppApi = spotifyAppApi(clientId = clientId, clientSecret = clientSecret).build(
            enableDefaultTokenRefreshProducerIfNoneExists = true
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                MainScreen()
            }
        }
    }

    private suspend fun searchForResult(query: String): SpotifySearchResult? {
        var res: SpotifySearchResult? = null

        if (::publicSpotifyAppApi.isInitialized) {
            // Otherwise, might block the main thread.
            res = withContext(Dispatchers.IO) {
                publicSpotifyAppApi.search.search(
                    query = query,
                    searchTypes = listOf(SearchApi.SearchType.Album).toTypedArray()
                )
            }
        }

        return res
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun MainScreen() {
        val textFieldQuery = remember { mutableStateOf("") }
        val foundStuff = remember { mutableListOf("") }
        val isLoading = remember { mutableStateOf(false) }

        LaunchedEffect(textFieldQuery.value) {
            if (textFieldQuery.value.isNotEmpty()) {
                // In order to show a loading Composable whilst results are fetched.
                isLoading.value = true

                delay(1000L) // Debounce for 1 second.
                val result = searchForResult(textFieldQuery.value)
                foundStuff.clear()

                // Just for testing.. teehee!
                if (result?.albums != null && result.albums?.size !!> 0) {
                    val firstAlbum = result.albums?.get(0)?.artists?.get(0)?.name + " - " +
                            result.albums?.get(0)?.name
                    foundStuff.addAll(listOf(firstAlbum))
                }

                isLoading.value = false
            }
        }

        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Start a Session")
            TextField(
                value = textFieldQuery.value,
                placeholder = {
                    Text("Click to start typing")
                },
                onValueChange = {
                    textFieldQuery.value = it
                }
            )

            when {
                textFieldQuery.value.isEmpty() -> {
                    Text("Type something you pagan") // When nothing's been typed yet.
                }
                isLoading.value -> {
                    CircularProgressIndicator() // Show that it's visibly fetching results
                }
                foundStuff.isNotEmpty() -> {
                    Text(foundStuff[0]) // Else, show the result.
                }
                else -> {
                    Text("No results found.") // Terrible search.
                }
            }
        }
    }
}