package com.asadshamsiev.spotifyexplorationapplication.tracklist.buttons

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.SimpleTrack
import com.asadshamsiev.spotifyexplorationapplication.utils.SimpleTrackWrapper
import com.asadshamsiev.spotifyexplorationapplication.utils.findFirstIndicesOfTracks
import com.asadshamsiev.spotifyexplorationapplication.utils.getExploreButtonOnClickFunction
import com.asadshamsiev.spotifyexplorationapplication.utils.getExploreProgressRunnable
import com.asadshamsiev.spotifyexplorationapplication.utils.getSpeedButtonOnClickFunction
import com.asadshamsiev.spotifyexplorationapplication.utils.getSpeedProgressRunnable
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel

@Composable
fun ButtonSection(
    viewModel: MainScreenViewModel,
    currentIntervalIndex: MutableState<Int>,
    isLoadingTracks: Boolean
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonRow(
            viewModel = viewModel,
            currentIntervalIndex = currentIntervalIndex,
            isLoadingTracks = isLoadingTracks
        )
    }
}

@Composable
fun ButtonRow(
    viewModel: MainScreenViewModel,
    currentIntervalIndex: MutableState<Int>,
    isLoadingTracks: Boolean
) {
    val currentAlbumTracks = remember { viewModel.currentAlbumTracks }
    val currentSpeedAlbumTracks = remember { viewModel.currentSpeedAlbumTracks }

    val trackStartIndices =
        remember { derivedStateOf { findFirstIndicesOfTracks(currentAlbumTracks) } }
    val trackStartSpeedIndices =
        remember { derivedStateOf { findFirstIndicesOfTracks(currentSpeedAlbumTracks) } }

    AnimatedVisibility(visible = isLoadingTracks, enter = fadeIn(animationSpec = tween(250))) {
        LoadingText()
    }
    AnimatedVisibility(visible = !isLoadingTracks, enter = fadeIn(animationSpec = tween(250))) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExploreAlbumButton(
                viewModel = viewModel,
                currentIntervalIndex = currentIntervalIndex,
                isLoading = isLoadingTracks,
                currentAlbumTracks = currentAlbumTracks,
                trackStartIndices = trackStartIndices
            )

            SpeedRunAlbumButton(
                viewModel = viewModel,
                currentIntervalIndex = currentIntervalIndex,
                currentSpeedAlbumTracks = currentSpeedAlbumTracks,
                trackStartIndices = trackStartSpeedIndices
            )
        }

    }
}

/* TODO: Speed mode button.
    This is going to be a bit more difficult, because SampleSong gets called in
    MainActivity's handleAlbumChange. I'll probably need 2 separate variables. */
@Composable
fun SpeedRunAlbumButton(
    viewModel: MainScreenViewModel,
    currentIntervalIndex: MutableState<Int>,
    currentSpeedAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    trackStartIndices: State<Map<String, Int>>
) {
    val handler = rememberUpdatedState(Handler(Looper.getMainLooper()))
    val buttonClicked = remember { mutableStateOf(false) }

    val spotifyAppRemote = viewModel.spotifyAppRemote

    val checkProgressRunnable = getSpeedProgressRunnable(
        spotifyAppRemote = spotifyAppRemote,
        currentSpeedAlbumTracks = currentSpeedAlbumTracks,
        currentIntervalIndex = currentIntervalIndex,
        viewModel = viewModel,
        handler = handler
    )

    val onClick = getSpeedButtonOnClickFunction(
        spotifyAppRemote = spotifyAppRemote,
        currentSpeedAlbumTracks = currentSpeedAlbumTracks,
        viewModel = viewModel,
        handler = handler,
        buttonClicked = buttonClicked,
        currentIntervalIndex = currentIntervalIndex,
        checkProgressRunnable = checkProgressRunnable
    )

    // If the button is clicked, and the explore session is ended, then remove all the stuff, and pause.
    LaunchedEffect(buttonClicked.value) {
        if (!viewModel.isSpeedSessionStarted) {
            try {
                handler.value.removeCallbacks(checkProgressRunnable)
                viewModel.currentIntervalIndex.value = 0

                // Go back to the first song.
                val firstTrackInAlbum: SimpleTrack =
                    currentSpeedAlbumTracks[currentIntervalIndex.value].first.track
                spotifyAppRemote?.playerApi?.play(firstTrackInAlbum.uri.uri)
                spotifyAppRemote?.playerApi?.pause()
            } catch (e: Exception) {
                Log.d("removeCallbacks", "Callback unsuccessfully removed: $e")
            }
        }
    }

    val currentTrack =
        if (currentIntervalIndex.value < currentSpeedAlbumTracks.size) {
            currentSpeedAlbumTracks[currentIntervalIndex.value].first
        } else {
            null
        }

    if (!viewModel.isSpeedSessionStarted) {
        Button(onClick = onClick) {
            Text("Speed")
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Started babe")

            Spacer(modifier = Modifier.size(8.dp))

            // This is the durations you see when an exploration session is started.
            DurationCards(
                trackStartIndices,
                currentSpeedAlbumTracks,
                currentIntervalIndex,
                currentTrack
            )
        }
    }
}

// When the tracks are being laid out, this is what shows.
@Composable
fun LoadingText() {
    // This is here so there's a smoother transition between the loading text and
    // the explore buttons.
    Button(
        enabled = false,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.Black
        ), onClick = {}
    ) {
        Text(
            "Loading.. 🤺",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}