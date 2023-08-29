package com.asadshamsiev.spotifyexplorationapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
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
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val clientId = "d6d33d89d3a044618291f268d1eea409"
    private val clientSecret = "58f5caf8a73b439689b108824daf4c79"
    private val redirectUri = "com.asadshamsiev.spotifyexplorationapplication://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var publicSpotifyAppApi: SpotifyAppApi? = null

    // We'll use this to tell if the local Spotify (1) thing (SpotifyAppRemote) doesn't work.
    private var localSpotifyDead = mutableStateOf(false)

    // This'll be for the search stuff (2).
    private var spotifyApiDead = mutableStateOf(false)

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
                localSpotifyDead.value = true
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
            spotifyApiDead.value = true
        }
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
                MainScreen(
                    spotifyApiDead.value,
                    localSpotifyDead.value
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
    fun SpotifyCard(
        artistName: String,
        albumName: String,
        link: String, // These'll eventually need defaults for if it craps out.
        modifier: Modifier = Modifier
    ) {
        Card(
            border = BorderStroke(1.dp, Color.Black),
            modifier = modifier.fillMaxWidth().padding(0.dp),
            shape = RoundedCornerShape(0),
        ) {
            Row(
                modifier = Modifier
                    .padding(0.dp)
                    .height(80.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                AsyncImage(
                    model = link,
                    contentDescription = null,
                    modifier = Modifier
                        .width(64.dp)
                        // .fillMaxHeight()
                        .border(
                            width = 1.dp,
                            color = Color.Black
                        )
                )
                Spacer(Modifier.size(16.dp))
                Column {
                    Text(artistName, Modifier.width(200.dp), fontSize = 16.sp, lineHeight = 12.sp)
                    Text(albumName, Modifier.width(200.dp), fontSize = 12.sp, lineHeight = 12.sp)
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun MainScreen(
        spotifyApiDead: Boolean,
        localSpotifyDead: Boolean
    ) {
        val textFieldQuery = remember { mutableStateOf("") }
        val foundStuff = remember { mutableListOf<Triple<String, String, String>>() }
        val isLoading = remember { mutableStateOf(false) }

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
                    val albumsList: ArrayList<Triple<String, String, String>> = arrayListOf()

                    for (i in 0 until minOf(3, result.albums!!.size)) {
                        val album = result.albums!![i]
                        val artistName = album.artists[0].name
                        val albumName = album.name
                        val image = album.images[0].url

                        albumsList.add(Triple(artistName, albumName, image))
                    }

                    foundStuff.addAll(albumsList)
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
            if (spotifyApiDead) {
                Text("Spotify cannot authenticate your account.")
            } else if (localSpotifyDead) {
                Text("You haven't got Spotify installed on your phone.")
            } else {
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
                            val (artistName, albumName, link) = infoTuple
                            SpotifyCard(
                                artistName = artistName,
                                albumName = albumName,
                                link = link
                            )
                        }
                    }

                    else -> {
                        Text("No results found.") // Terrible search.
                    }
                }
            }
        }
    }
}