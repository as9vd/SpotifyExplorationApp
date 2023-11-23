package com.asadshamsiev.spotifyexplorationapplication

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import com.asadshamsiev.spotifyexplorationapplication.utils.TrackUtils
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote

// TODO: Need to deal with A) songs that can't play. Like that Tory Lanez album with The Colour Violet.
// TODO: Also, B) podcasts.
@Composable
fun ExploreAlbumButton(
    viewModel: MainScreenViewModel,
    currentIntervalIndex: MutableState<Int>,
    isLoading: Boolean
) {
    val handler = rememberUpdatedState(Handler(Looper.getMainLooper()))
    val buttonClicked = remember { mutableStateOf(false) }

    val currentAlbumTracks = remember { viewModel.currentAlbumTracks }
    val trackStartIndices =
        remember { derivedStateOf { findFirstIndicesOfTracks(currentAlbumTracks) } }

    val spotifyAppRemote = viewModel.spotifyAppRemote

    val checkProgressRunnable = getProgressRunnable(
        spotifyAppRemote = spotifyAppRemote,
        currentAlbumTracks = currentAlbumTracks,
        currentIntervalIndex = currentIntervalIndex,
        viewModel = viewModel,
        handler = handler
    )

    val onClick = getButtonOnClickFunction(
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
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(250))
        ) {
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

        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(animationSpec = tween(250))
        ) {
            if (!viewModel.isExploreSessionStarted) {
                Button(
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                    border = BorderStroke(1.dp, Color.Black),
                    onClick = onClick,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {}
                ) {
                    Text("Start Exploring")
                }

            } else {
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
        }
    }
}

fun getButtonOnClickFunction(
    spotifyAppRemote: SpotifyAppRemote?,
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    viewModel: MainScreenViewModel,
    handler: State<Handler>,
    buttonClicked: MutableState<Boolean>,
    currentIntervalIndex: MutableState<Int>,
    checkProgressRunnable: Runnable
): () -> Unit {
    return {
        val remoteApiConnected = (spotifyAppRemote != null && spotifyAppRemote.isConnected)
        if (!viewModel.isExploreSessionStarted && remoteApiConnected) {
            // Reset the index. Will start from the beginning, at the top of the list.
            viewModel.currentIntervalIndex.value = 0

            // Get the first track and its uri, because we'll play it.
            val firstTrack = currentAlbumTracks[0].first
            val firstTrackUri = firstTrack.track.uri.uri

            spotifyAppRemote!!.playerApi.play(firstTrackUri)
                ?.apply {
                    val initialInterval = currentAlbumTracks[currentIntervalIndex.value].second
                    val startOfFirstInterval = TrackUtils.durationToMs(initialInterval.first)

                    handler.value.postDelayed({
                        spotifyAppRemote.playerApi.seekTo(startOfFirstInterval)
                    }, 500)

                    handler.value.post(checkProgressRunnable)

                    // If you can load it, then good.
                    viewModel.isLocalSpotifyDead = false
                }?.setErrorCallback {
                    // If you can't play this, then the local API is screwed.
                    viewModel.isLocalSpotifyDead = true
                }
            buttonClicked.value = true
        } else if (remoteApiConnected) {
            spotifyAppRemote?.playerApi?.pause()

            handler.value.removeCallbacks(checkProgressRunnable)
            buttonClicked.value = false

            viewModel.isLocalSpotifyDead = false
        } else {
            handler.value.removeCallbacks(checkProgressRunnable) // Just in case.
            Log.d("onClick", "Remote API not connected!")

            viewModel.isLocalSpotifyDead = true
        }

        viewModel.setIsExploreSessionStarted(!viewModel.isExploreSessionStarted)
    }
}

fun getProgressRunnable(
    spotifyAppRemote: SpotifyAppRemote?,
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    currentIntervalIndex: MutableState<Int>,
    viewModel: MainScreenViewModel,
    handler: State<Handler>
): Runnable {
    return object : Runnable {
        override fun run() {
            if (spotifyAppRemote != null && spotifyAppRemote.isConnected) {
                try {
                    spotifyAppRemote.playerApi.playerState?.setResultCallback { state ->
                        val currentPosition = state.playbackPosition

                        // This is the paired interval (e.g. <"1:28", "2:56">).
                        val currentInterval =
                            currentAlbumTracks[currentIntervalIndex.value].second

                        val endOfCurrentInterval: Long =
                            TrackUtils.durationToMs(currentInterval.second)

                        // If we're past the interval, then move on to the next one.
                        if (currentPosition >= endOfCurrentInterval) {
                            viewModel.currentIntervalIndex.value = currentIntervalIndex.value + 1

                            // If there's another interval to be played, then play it.
                            val amountOfIntervals: Int = currentAlbumTracks.size
                            val isAtEnd: Boolean = currentIntervalIndex.value >= amountOfIntervals
                            if (!isAtEnd) {
                                val trackToBePlayed =
                                    currentAlbumTracks[currentIntervalIndex.value].first
                                val previousTrackPlayed =
                                    currentAlbumTracks[currentIntervalIndex.value - 1].first
                                val nextInterval =
                                    currentAlbumTracks[currentIntervalIndex.value].second
                                val startOfNextInterval: Long =
                                    TrackUtils.durationToMs(nextInterval.first)

                                if (trackToBePlayed == previousTrackPlayed) {
                                    spotifyAppRemote.playerApi.seekTo(startOfNextInterval)
                                } else {
                                    // First interval starts at 0:00 (in this implementation), so
                                    // no need to skip now.
                                    spotifyAppRemote.playerApi.play(trackToBePlayed.track.uri.uri)
                                        .setErrorCallback {
                                            Log.d(
                                                "errorCallback",
                                                "Couldn't seek to the start of the next for the new song."
                                            )
                                        }
                                }
                            } else {
                                // If you're at the end (e.g. there are no more tracks), just
                                // stop the exploration process.
                                spotifyAppRemote.playerApi.pause()

                                // To reset. Can explore again afterward.
                                viewModel.setIsExploreSessionStarted(false)

                                handler.value.removeCallbacks(this)
                                return@setResultCallback
                            }

                        }


                        if (viewModel.isExploreSessionStarted) {
                            handler.value.postDelayed(this, 500)
                        }
                    }?.setErrorCallback {
                        Log.d("it", it.toString())
                    }
                } catch (e: Exception) {
                    Log.d("checkProgressRunnable", "checkProgressRunnable failed: $e")
                }
            } else {
                Log.d(
                    "checkProgressRunnable",
                    "checkProgressRunnable failed, as SpotifyAppRemote is either null or not connected."
                )
            }
        }
    }
}

fun findFirstIndicesOfTracks(currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>): Map<String, Int> {
    val seenTracks = mutableSetOf<String>()
    val firstIndices = mutableMapOf<String, Int>()

    currentAlbumTracks.forEachIndexed { index, (track, _) ->
        val trackId = track.track.id
        if (trackId !in seenTracks) {
            seenTracks.add(trackId)
            firstIndices[trackId] = index
        }
    }

    return firstIndices
}