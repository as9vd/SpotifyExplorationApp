package com.asadshamsiev.spotifyexplorationapplication.tracklist.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asadshamsiev.spotifyexplorationapplication.utils.SimpleTrackWrapper
import com.asadshamsiev.spotifyexplorationapplication.utils.findFirstIndicesOfTracks
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
                currentSpeedAlbumTracks
            )
        }

    }
}

/* TODO: Speed mode button.
    This is going to be a bit more difficult, because SampleSong gets called in
    MainActivity's handleAlbumChange. I'll probably need 2 separate variables. */
@Composable
fun SpeedRunAlbumButton(
    currentSpeedAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>
) {
    val buttonClicked = remember { mutableStateOf(false) }

    Button(onClick = {
        buttonClicked.value = !buttonClicked.value
        print(currentSpeedAlbumTracks)
    }) {
        Text("Speed")
    }

    Column {
        Text("Size of this fucking array:" + currentSpeedAlbumTracks.size)
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