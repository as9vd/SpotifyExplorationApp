package com.asadshamsiev.spotifyexplorationapplication.tracklist

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.asadshamsiev.spotifyexplorationapplication.tracklist.buttons.ButtonSection
import com.asadshamsiev.spotifyexplorationapplication.utils.SimpleTrackWrapper
import com.asadshamsiev.spotifyexplorationapplication.utils.TrackUtils
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TrackListSection(
    viewModel: MainScreenViewModel,
    isLoadingTracks: Boolean,
    batchIndex: Int
) {
    val uniqueTracks = viewModel.uniqueTracks
    val tracksInit = uniqueTracks?.isNotEmpty()

    // This is useless. All it does is ensure that this thing is forced to recompose when batchIndex
    // updates. I'll need a better way to do that in the future.
    val batchIndexFiller = batchIndex

    /* 2 SUBSECTIONS WITHIN THE TRACKLISTSECTION:
       1. The Buttons,
       2. The actual track Cards.
    */
    if (tracksInit == true) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // This button is the thing that actually starts the sampling.
            val currentIntervalIndex = viewModel.currentIntervalIndex

            ButtonSection(
                viewModel = viewModel,
                currentIntervalIndex = currentIntervalIndex,
                isLoadingTracks = isLoadingTracks
            )

            Spacer(modifier = Modifier.size(8.dp))

            TrackList(uniqueTracks = uniqueTracks, viewModel = viewModel)

            Spacer(modifier = Modifier.size(8.dp))
        }
    } else {
        // TODO: When the thing is just not going to load (e.g. past 10 seconds), just give up.
        CircularProgressIndicator()
        Text(
            "Data is loading. If this goes on for a while, there's a good chance your internet signal is weak, mate.",
            fontStyle = FontStyle.Italic
        )
    }

    Spacer(modifier = Modifier.size(8.dp)) // A little space on the bottom.
}

@Composable
fun TrackList(uniqueTracks: List<SimpleTrackWrapper>, viewModel: MainScreenViewModel) {
    Box(Modifier.border(BorderStroke(1.dp, Color.Black))) {
        Column {
            for (track in uniqueTracks) {
                val isVisible = remember { mutableStateOf(false) }
                LaunchedEffect(track.track.id) {
                    isVisible.value = true
                }

                // For each track in the current album,
                // create a TrackCard for it.
                AnimatedVisibility(
                    visible = isVisible.value,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -20 }
                    )
                ) {
                    key(track.track.id) {
                        TrackCard(
                            track = track,
                            viewModel = viewModel
                        )

                        // Manual border.
                        Divider(
                            color = Color.Black,
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackCard(
    track: SimpleTrackWrapper,
    viewModel: MainScreenViewModel
) {
    val isPlaying = remember {
        derivedStateOf {
            viewModel.trackUri == track.track.uri.uri
        }
    }
    val spotifyAppRemote = remember { viewModel.spotifyAppRemote }

    // If the current track uri is equal to this track's, then it isPlaying, which'll trigger animation.
    LaunchedEffect(track) {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
            val validComposable = (state.track != null)
            if (validComposable) {
                viewModel.isLocalSpotifyDead = false
            }
        }?.setErrorCallback {
            // If you can't play, it's dead.
            Log.d("eventCallback Error", it.toString())
            viewModel.isLocalSpotifyDead = true
        }

    }

    val screwed = remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                try {
                    spotifyAppRemote?.playerApi
                        ?.play(track.track.uri.uri)
                        ?.setErrorCallback {
                            Log.d("it", it.toString())
                        }
                    screwed.value = false
                } catch (e: Exception) {
                    Log.d("onClick", "Can't play specified song: $e")
                    screwed.value = true
                }

                // Clicking a track will interrupt an explore session.
                // Even if the remote API can't call it. Makes no difference. It will be false.
                viewModel.setIsExploreSessionStarted(false)
                viewModel.currentIntervalIndex.value = 0 // Sets index to 0.
            }
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Crossfade(
                targetState = isPlaying.value,
                label = "Transition the Bone",
                animationSpec = tween(250)
            ) { playing ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fontWeight = if (playing) 900 else 400

                    Text(
                        "${track.track.trackNumber}.",
                        fontWeight = FontWeight(fontWeight),
                        modifier = Modifier
                            .padding(12.dp)
                            .weight(0.15f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "${track.track.name} (${TrackUtils.msToDuration(track.track.length)})",
                        fontWeight = FontWeight(fontWeight),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(12.dp)
                            .weight(0.85f)
                    )
                }
            }
        }
    }
}
