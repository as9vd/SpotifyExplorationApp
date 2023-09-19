package com.asadshamsiev.spotifyexplorationapplication

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
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
import com.adamratzman.spotify.SpotifyAppApi
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    spotifyAppRemote: SpotifyAppRemote?,
    publicSpotifyAppApi: SpotifyAppApi?
) {
    val textFieldQuery = remember { mutableStateOf(UNINIT_STR) }
    val foundStuff = remember { mutableListOf<List<String>>() }
    val isLoading = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Whenever the query gets updated.
    LaunchedEffect(textFieldQuery.value) {
        // If A) something has been typed,
        // or B) the entire query hasn't been deleted.
        if (textFieldQuery.value.isNotEmpty()) {
            // In order to show a loading Composable whilst results are fetched.
            isLoading.value = true

            // Give it a second before conducting another search.
            delay(1000L)
            val result = viewModel.searchForResult(publicSpotifyAppApi, textFieldQuery.value)
            foundStuff.clear()

            val isValidResult: Boolean = (result?.albums != null && result.albums?.size!! > 0)
            if (isValidResult) {
                val albumsList: ArrayList<List<String>> = arrayListOf()

                for (i in 0 until minOf(3, result!!.albums!!.size)) {
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

            // No longer loading, so no need to show circular loading animation.
            isLoading.value = false
        }
    }

    val spotifyApiDead = viewModel.isSpotifyApiDead.collectAsState().value
    val localSpotifyDead: Boolean = viewModel.isLocalSpotifyDead.collectAsState().value

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.size(8.dp))

        // These errors only show when the
        // 1. local phone API is dead or
        // 2. the public API is dead.
        SearchConditionalErrors(
            spotifyApiDead = spotifyApiDead,
            localSpotifyDead = localSpotifyDead
        )

        if (!spotifyApiDead && !localSpotifyDead) {
            AlbumSection(
                foundStuff = foundStuff,
                isLoading = isLoading,
                spotifyAppRemote = spotifyAppRemote,
                textFieldQuery = textFieldQuery,
                viewModel = viewModel
            )

            TrackListSection(
                spotifyAppRemote = spotifyAppRemote,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun AlbumSection(
    foundStuff:MutableList<List<String>>,
    isLoading: MutableState<Boolean>,
    spotifyAppRemote: SpotifyAppRemote?,
    textFieldQuery: MutableState<String>,
    viewModel: MainScreenViewModel
) {
    SearchBox(viewModel = viewModel, textFieldQuery = textFieldQuery)
    AlbumCardResults(
        spotifyAppRemote = spotifyAppRemote,
        textFieldQuery = textFieldQuery,
        isLoading = isLoading,
        foundStuff = foundStuff
    )
}

@Composable
fun SearchConditionalErrors(spotifyApiDead: Boolean, localSpotifyDead: Boolean) {
    if (spotifyApiDead) {
        Text("Spotify cannot authenticate your account.")
    } else if (localSpotifyDead) {
        Text("You haven't got Spotify installed on your phone.")
    } else {
        // no-op. Don't show anything.
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    viewModel: MainScreenViewModel,
    textFieldQuery: MutableState<String>
) {
    val coroutineScope = rememberCoroutineScope()

    val (enlargeTrigger, setEnlargeTrigger) = remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(
        targetValue = if (enlargeTrigger) 1.25f else 1f, // 1.1x when enlarged, 1x for normal.
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ), label = "Get Big"
    )

    Text(
        "🦧", fontSize = 28.sp, letterSpacing = 0.25.sp, modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { _ ->
                        tryAwaitRelease()
                        setEnlargeTrigger(true)
                        coroutineScope.launch {
                            delay(750)
                            setEnlargeTrigger(false)
                        }
                        viewModel.incrementColourIndex()
                    }
                )
            }
            .graphicsLayer(scaleX = scale, scaleY = scale)
    )
    TextField(
        value = textFieldQuery.value,
        placeholder = {
            Text("Click to start typing!", fontSize = 18.sp)
        },
        onValueChange = {
            textFieldQuery.value = it
        }
    )
}

@Composable
fun AlbumCardResults(
    spotifyAppRemote: SpotifyAppRemote?,
    textFieldQuery: MutableState<String>,
    isLoading: MutableState<Boolean>,
    foundStuff: MutableList<List<String>>
) {
    when {
        textFieldQuery.value.isEmpty() -> {
            // no-op.
        }

        isLoading.value -> {
            CircularProgressIndicator() // Show that it's visibly fetching results.
        }

        foundStuff.isNotEmpty() -> {
            // Else, show the result.
            for (infoTuple in foundStuff) {
                val (artistName, albumName, link, uri) = infoTuple

                AlbumCard(
                    artistName = artistName,
                    albumName = albumName,
                    link = link,
                    onClick = { spotifyAppRemote?.playerApi?.play(uri) }
                )
            }
        }

        else -> {
            Text("No results found.") // Terrible search.
        }
    }
}