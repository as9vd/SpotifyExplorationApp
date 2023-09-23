package com.asadshamsiev.spotifyexplorationapplication

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    batchIndex: Int
) {
    val textFieldQuery = remember { mutableStateOf(UNINIT_STR) }
    val foundStuff = remember { mutableListOf<List<String>>() }
    val isLoading = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val publicSpotifyAppApi = viewModel.publicSpotifyAppApi

    // Whenever the query gets updated, run this thing.
    LaunchedEffect(textFieldQuery.value) {
        // If A) something has been typed,
        // or B) the entire query hasn't been deleted.
        if (textFieldQuery.value.isNotEmpty()) {
            // In order to show a loading Composable whilst results are fetched.
            isLoading.value = true

            // Give it 2 seconds before conducting another search.
            delay(2000L)
            foundStuff.clear()

            // Use the public Spotify API to fetch results related to the query.
            val result = viewModel.searchForResult(publicSpotifyAppApi, textFieldQuery.value)

            // If the result isn't null, and the result yields album(s), then it's valid.
            // Otherwise, if it's not valid, then at least it's handled below, with
            // "No results found".
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

    val spotifyApiDead = viewModel.isSpotifyApiDead
    val localSpotifyDead: Boolean = viewModel.isLocalSpotifyDead

    Crossfade(targetState = spotifyApiDead || localSpotifyDead, label = "Error(s) Transition") { state ->
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
            if (state) {
                SearchConditionalErrors(
                    spotifyApiDead = spotifyApiDead,
                    localSpotifyDead = localSpotifyDead
                )
            } else {
                if (!spotifyApiDead && !localSpotifyDead) {
                    AlbumSection(
                        foundStuff = foundStuff,
                        isLoading = isLoading,
                        textFieldQuery = textFieldQuery,
                        viewModel = viewModel
                    )

                    TrackListSection(
                        viewModel = viewModel,
                        batchIndex = batchIndex
                    )
                }
            }
        }
    }
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