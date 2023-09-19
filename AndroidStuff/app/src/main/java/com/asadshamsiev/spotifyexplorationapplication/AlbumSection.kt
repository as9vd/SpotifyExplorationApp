package com.asadshamsiev.spotifyexplorationapplication

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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