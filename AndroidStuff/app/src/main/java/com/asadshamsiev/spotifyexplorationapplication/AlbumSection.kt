package com.asadshamsiev.spotifyexplorationapplication

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val CARD_HEIGHT = 80
const val CARD_PADDING = 8

@Composable
fun AlbumSection(
    foundStuff: List<List<String>>,
    isLoading: MutableState<Boolean>,
    textFieldQuery: MutableState<String>,
    viewModel: MainScreenViewModel
) {
    val textFieldEmpty = remember { derivedStateOf { textFieldQuery.value.isEmpty() } }

    SearchBox(viewModel = viewModel, textFieldQuery = textFieldQuery)
    AlbumCardResults(
        textFieldEmpty = textFieldEmpty.value,
        isLoading = isLoading,
        foundStuff = foundStuff,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    viewModel: MainScreenViewModel,
    textFieldQuery: MutableState<String>
) {
    AllSeeingGorilla(viewModel = viewModel)
    TextField(
        value = textFieldQuery.value,
        placeholder = { Text("Click to start typing!", fontSize = 18.sp) },
        onValueChange = { textFieldQuery.value = it }
    )
}

@Composable
fun AllSeeingGorilla(viewModel: MainScreenViewModel) {
    val coroutineScope = rememberCoroutineScope()

    // This'll make the All-Seeing Gorilla get big when clicked!
    val (enlargeTrigger, setEnlargeTrigger) = remember { mutableStateOf(false) }
    val scale: Float by animateFloatAsState(
        targetValue = if (enlargeTrigger) 1.25f else 1f, // 1.25x the size when enlarged, 1x for normal.
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ), label = "Get Big"
    )

    // The All-Seeing Gorilla.
    // Click it, and the colour scheme changes.
    Text(
        "🦧", fontSize = 28.sp, letterSpacing = 0.25.sp, modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { _ ->
                        tryAwaitRelease()
                        setEnlargeTrigger(true)
                        coroutineScope.launch {
                            delay(500)
                            setEnlargeTrigger(false)
                        }
                        viewModel.incrementColourIndex()
                    }
                )
            }
            .graphicsLayer(scaleX = scale, scaleY = scale)
    )
}

@Composable
fun AlbumCardResults(
    textFieldEmpty: Boolean,
    isLoading: MutableState<Boolean>,
    foundStuff: List<List<String>>,
    viewModel: MainScreenViewModel
) {
    val spotifyAppRemote = viewModel.spotifyAppRemote

    when {
        textFieldEmpty -> {
            // no-op. If nothing's typed, then just chill.
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
                    onClick = {
                        spotifyAppRemote?.playerApi?.play(uri)?.setResultCallback {
                            // If you can load it, good.
                            viewModel.isLocalSpotifyDead = false
                            viewModel.setIsExploreSessionStarted(false)
                        }?.setErrorCallback {
                            // If you can't load the album, then the remote API is dead.
                            viewModel.isLocalSpotifyDead = true
                        }
                    }
                )
            }
        }

        else -> {
            Text("No results found.") // Terrible search.
        }
    }
}

// TODO: Give AlbumCard a cool animation when clicked.
@Composable
fun AlbumCard(
    artistName: String,
    albumName: String,
    onClick: () -> Unit,
    link: String, // These'll eventually need defaults for if it craps out.
    modifier: Modifier = Modifier
) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onClick()
                    }
                )
            },
        shape = RoundedCornerShape(0),
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp)
                .height(CARD_HEIGHT.dp)
                .fillMaxWidth()
                .padding(CARD_PADDING.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // This is the album cover.
            AsyncImage(
                model = link,
                contentDescription = null,
                modifier = Modifier
                    .width(64.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black
                    )
            )
            Spacer(Modifier.size(16.dp))
            // Name of album and artist. Rudimentary stuff.
            Column {
                Text(artistName, Modifier.width(200.dp), fontSize = 16.sp, lineHeight = 12.sp)
                Text(albumName, Modifier.width(200.dp), fontSize = 12.sp, lineHeight = 12.sp)
            }
        }
    }
}