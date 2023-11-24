package com.asadshamsiev.spotifyexplorationapplication.tracklist.buttons

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.SimpleTrack
import com.asadshamsiev.spotifyexplorationapplication.utils.*
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel

// Potential issues here with songs that can't play. Like that Tory Lanez album with
// The Colour Violet. Also, podcasts.
@Composable
fun ExploreAlbumButton(
    viewModel: MainScreenViewModel,
    currentIntervalIndex: MutableState<Int>,
    isLoading: Boolean,
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    trackStartIndices: State<Map<String, Int>>
) {
    val handler = rememberUpdatedState(Handler(Looper.getMainLooper()))
    val buttonClicked = remember { mutableStateOf(false) }

    val spotifyAppRemote = viewModel.spotifyAppRemote

    val checkProgressRunnable = getExploreProgressRunnable(
        spotifyAppRemote = spotifyAppRemote,
        currentAlbumTracks = currentAlbumTracks,
        currentIntervalIndex = currentIntervalIndex,
        viewModel = viewModel,
        handler = handler
    )

    val onClick = getExploreButtonOnClickFunction(
        spotifyAppRemote = spotifyAppRemote,
        currentAlbumTracks = currentAlbumTracks,
        viewModel = viewModel,
        handler = handler,
        buttonClicked = buttonClicked,
        currentIntervalIndex = currentIntervalIndex,
        checkProgressRunnable = checkProgressRunnable
    )

    // If the button is clicked, and the explore session is ended, then remove all the stuff, and pause.
    LaunchedEffect(buttonClicked.value) {
        if (!viewModel.isExploreSessionStarted) {
            try {
                handler.value.removeCallbacks(checkProgressRunnable)
                viewModel.currentIntervalIndex.value = 0

                // Go back to the first song.
                val firstTrackInAlbum: SimpleTrack =
                    currentAlbumTracks[currentIntervalIndex.value].first.track
                spotifyAppRemote?.playerApi?.play(firstTrackInAlbum.uri.uri)
                spotifyAppRemote?.playerApi?.pause()
            } catch (e: Exception) {
                Log.d("removeCallbacks", "Callback unsuccessfully removed: $e")
            }
        }
    }

    val currentTrack =
        if (currentIntervalIndex.value < currentAlbumTracks.size) {
            currentAlbumTracks[currentIntervalIndex.value].first
        } else {
            null
        }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!viewModel.isExploreSessionStarted) {
            UnstartedExploreSession(onClick = onClick)
        } else {
            StartedExploreSession(
                onClick = onClick,
                trackStartIndices = trackStartIndices,
                currentAlbumTracks = currentAlbumTracks,
                currentIntervalIndex = currentIntervalIndex,
                currentTrack = currentTrack
            )
        }
    }
}

// This is the Composable that shows when the Explore button hasn't been pressed.
@Composable
fun UnstartedExploreSession(onClick: () -> Unit) {
    Button(
        elevation = ButtonDefaults.elevatedButtonElevation(),
        border = BorderStroke(1.dp, Color.Black),
        onClick = onClick,
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) {}
    ) {
        Text("Explore")
    }
}

// This is the Composable that shows when the Explore button is clicked.
@Composable
fun StartedExploreSession(
    onClick: () -> Unit,
    trackStartIndices: State<Map<String, Int>>,
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    currentIntervalIndex: MutableState<Int>,
    currentTrack: SimpleTrackWrapper?
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            elevation = ButtonDefaults.elevatedButtonElevation(),
            border = BorderStroke(1.dp, Color.Black),
            onClick = onClick
        ) {
            Text("Stop Exploring")
        }

        Spacer(modifier = Modifier.size(8.dp))

        // This is the durations you see when an exploration session is started.
        DurationCards(
            trackStartIndices,
            currentAlbumTracks,
            currentIntervalIndex,
            currentTrack
        )
    }
}

