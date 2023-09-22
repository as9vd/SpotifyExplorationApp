package com.asadshamsiev.spotifyexplorationapplication

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.SimpleTrack
import com.asadshamsiev.spotifyexplorationapplication.utils.TrackUtils
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TrackListSection(
    // spotifyAppRemote: SpotifyAppRemote? = null,
    viewModel: MainScreenViewModel
) {
    val currentAlbumTracks: List<Pair<SimpleTrack, Pair<String, String>>> =
        viewModel.currentAlbumTracks
    val tracksInit = currentAlbumTracks.isNotEmpty()

    // This stuff down here is to make sure TrackCards don't get unnecessarily recomposed.
    // It's like a pyramid, 5 -> 4 -> 3 -> 2 -> 1.
    val startIndex = remember { mutableStateOf(0) }
    LaunchedEffect(currentAlbumTracks) {
        // Pseudocode: for (startIndex to endTrackIndex + 1)
        // When 1st batch comes in, endTrackIndex = currentAlbumTracks, startIndex still 0.
        // When 2nd batch comes in, endTrackIndex = currentAlbumTracks, startIndex now 4.
        // So endTrackIndex is unnecessary. startIndex only updates past first batch (+= 4).
        // Need a check for if i > endTrackIndex + 1 in loop, for non-multiples of 4.
        val isFirstBatch: Boolean =
            (currentAlbumTracks.isNotEmpty()) && (currentAlbumTracks.size < 4)
        if (!isFirstBatch) {
            startIndex.value += 4
        }
    }

    // Isolate the unique tracks.
    val uniqueTracks = viewModel.uniqueTracks

    if (tracksInit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // This button is the thing that actually starts the sampling.
            ExploreAlbumButton(
                currentAlbumTracks = currentAlbumTracks,
                // spotifyAppRemote = spotifyAppRemote,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.size(8.dp))
            Box(Modifier.border(BorderStroke(1.dp, Color.Black))) {
                Column {
                    // Need one of these at the top.
                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )

                    for (track in uniqueTracks) {
                        // For each track in the current album,
                        // create a TrackCard for it.
                        key(track.id) {
                            TrackCard(
                                // spotifyAppRemote = spotifyAppRemote,
                                track = track,
                                viewModel = viewModel
                            )

                            // Manual border, because it's not like HTML/CSS at all.
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
            Spacer(modifier = Modifier.size(8.dp))
        }
    } else {
        // I'll need something here for when it's a A) podcast
        // or B) when the thing is just not going to load (e.g. past 10 seconds, just give up).
        CircularProgressIndicator()
        Text(
            "Data is loading. If this goes on for a while, there's a good chance your internet signal is weak, mate.",
            fontStyle = FontStyle.Italic
        )
    }

    Spacer(modifier = Modifier.size(8.dp)) // A little space on the bottom.
}

@Composable
fun TrackCard(
    // spotifyAppRemote: SpotifyAppRemote? = null,
    track: SimpleTrack,
    viewModel: MainScreenViewModel
) {
    val isPlaying = remember { mutableStateOf(false) }
    val spotifyAppRemote = viewModel.spotifyAppRemote

    // If the current track uri is equal to this track's, then it isPlaying, which'll trigger animation.
    LaunchedEffect(track) {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
            val validComposable = (state.track != null)
            if (validComposable) {
                isPlaying.value = (state.track.uri == track.uri.uri)
                viewModel.isLocalSpotifyDead = false
            }
        }?.setErrorCallback {
            // If you can't play, it's dead.
            Log.d("eventCallback Error", it.toString())
            viewModel.isLocalSpotifyDead = true
        }

    }

    val infiniteTransition = rememberInfiniteTransition(label = "Harlem Shake (Infinite Edition)")
    val shake by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(75, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "Harlem Shake"
    )

    val screwed = remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                try {
                    spotifyAppRemote?.playerApi
                        ?.play(track.uri.uri)
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
                viewModel.currentIntervalIndex = 0 // Sets index to 0.
            }
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Crossfade(
                targetState = isPlaying.value,
                label = "Transition the Bone",
                animationSpec = tween(1000)
            ) { playing ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!playing) {
                        Text(
                            "${track.trackNumber}.",
                            modifier = Modifier
                                .padding(12.dp)
                                .weight(0.15f),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // If the track is playing, show a bone instead of the track, and shake!
                        Text(
                            "🦴",
                            modifier = Modifier
                                .padding(12.dp)
                                .weight(0.15f)
                                .graphicsLayer(rotationZ = shake),
                            textAlign = TextAlign.Center
                        )
                    }

                    val fontWeight = if (isPlaying.value) 700 else 400
                    Text(
                        "${track.name} (${TrackUtils.msToDuration(track.length)})",
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

// TODO: Need to deal with A) songs that can't play. Like that Tory Lanez album with The Colour Violet.
// TODO: Also, B) podcasts.
@Composable
fun ExploreAlbumButton(
    // spotifyAppRemote: SpotifyAppRemote?,
    currentAlbumTracks: List<Pair<SimpleTrack, Pair<String, String>>>,
    viewModel: MainScreenViewModel
) {
    val handler = rememberUpdatedState(Handler(Looper.getMainLooper()))
    val screwed = remember { mutableStateOf(false) }
    val buttonClicked = remember { mutableStateOf(false) }
    val currentIntervalIndex = viewModel.currentIntervalIndex

    val trackStartIndices =
        remember { mutableStateOf(findFirstIndicesOfTracks(currentAlbumTracks)) }

    LaunchedEffect(currentAlbumTracks) {
        trackStartIndices.value = findFirstIndicesOfTracks(currentAlbumTracks)
    }

    val spotifyAppRemote = viewModel.spotifyAppRemote

    // TODO: Fix this shit and the onClick to reflect the new currentAlbumTracks format.
    val checkProgressRunnable = object : Runnable {
        override fun run() {
            if (spotifyAppRemote != null && spotifyAppRemote.isConnected) {
                try {
                    spotifyAppRemote.playerApi.playerState?.setResultCallback { state ->
                        val currentPosition = state.playbackPosition

                        // This is the paired interval (e.g. <"1:28", "2:56">).
                        val currentInterval =
                            currentAlbumTracks[currentIntervalIndex].second

                        val endOfCurrentInterval: Long =
                            TrackUtils.durationToMs(currentInterval.second)

                        // If we're past the interval, then move on to the next one.
                        if (currentPosition >= endOfCurrentInterval) {
                            viewModel.currentIntervalIndex = currentIntervalIndex + 1

                            // If there's another interval to be played, then play it.
                            val amountOfIntervals: Int = currentAlbumTracks.size
                            val isAtEnd: Boolean = currentIntervalIndex >= amountOfIntervals
                            if (!isAtEnd) {
                                val trackToBePlayed =
                                    currentAlbumTracks[currentIntervalIndex].first
                                val previousTrackPlayed =
                                    currentAlbumTracks[currentIntervalIndex - 1].first
                                val nextInterval =
                                    currentAlbumTracks[currentIntervalIndex].second
                                val startOfNextInterval: Long =
                                    TrackUtils.durationToMs(nextInterval.first)

                                if (trackToBePlayed == previousTrackPlayed) {
                                    spotifyAppRemote.playerApi.seekTo(startOfNextInterval)
                                } else {
                                    // First interval starts at 0:00 (in this implementation), so
                                    // no need to skip now.
                                    spotifyAppRemote.playerApi.play(trackToBePlayed.uri.uri)
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
                            screwed.value = false
                        }
                    }?.setErrorCallback {
                        Log.d("it", it.toString())
                    }
                } catch (e: Exception) {
                    Log.d("checkProgressRunnable", "checkProgressRunnable failed: $e")
                    screwed.value = true
                }
            } else {
                Log.d(
                    "checkProgressRunnable",
                    "checkProgressRunnable failed, as SpotifyAppRemote is either null or not connected."
                )
                screwed.value = true
            }
        }
    }

    val onClick: () -> Unit = {
        val remoteApiConnected = (spotifyAppRemote != null && spotifyAppRemote.isConnected)
        if (!viewModel.isExploreSessionStarted && remoteApiConnected) {
            // Reset the index. Will start from the beginning, at the top of the list.
            viewModel.currentIntervalIndex = 0

            // Get the first track and its uri, because we'll play it.
            val firstTrack = currentAlbumTracks[0].first
            val firstTrackUri = firstTrack.uri.uri

            spotifyAppRemote!!.playerApi.play(firstTrackUri)
                ?.apply {
                    val initialInterval = currentAlbumTracks[currentIntervalIndex].second
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
            screwed.value = false
            buttonClicked.value = true
        } else if (remoteApiConnected) {
            spotifyAppRemote?.playerApi?.pause()
            // TODO: Fix this so the skipping stuff stops when no longer in exploration mode.
            handler.value.removeCallbacks(checkProgressRunnable)
            screwed.value = false
            buttonClicked.value = false

            viewModel.isLocalSpotifyDead = false
        } else {
            handler.value.removeCallbacks(checkProgressRunnable) // Just in case.
            Log.d("onClick", "Remote API not connected!")
            screwed.value = true

            viewModel.isLocalSpotifyDead = true
        }

        viewModel.setIsExploreSessionStarted(!viewModel.isExploreSessionStarted)
    }

    // If the button is clicked, and the explore session is ended, then remove all the stuff, and pause.
    LaunchedEffect(buttonClicked.value) {
        if (!viewModel.isExploreSessionStarted) {
            try {
                handler.value.removeCallbacks(checkProgressRunnable)
                viewModel.currentIntervalIndex = 0

                // Go back to the first song.
                val firstTrackInAlbum: SimpleTrack =
                    currentAlbumTracks[currentIntervalIndex].first
                spotifyAppRemote?.playerApi?.play(firstTrackInAlbum.uri.uri)
                spotifyAppRemote?.playerApi?.pause()
            } catch (e: Exception) {
                Log.d("removeCallbacks", "Callback unsuccessfully removed: $e")
            }
        }
    }

    val currentTrack =
        if (currentIntervalIndex < currentAlbumTracks.size) {
            currentAlbumTracks[currentIntervalIndex].first
        } else {
            null
        }

    if (!viewModel.isExploreSessionStarted) {
        Button(
            elevation = ButtonDefaults.elevatedButtonElevation(),
            border = BorderStroke(1.dp, Color.Black),
            onClick = onClick
        ) {
            Text("Start Exploring")
        }
    } else {
        Button(
            elevation = ButtonDefaults.elevatedButtonElevation(),
            border = BorderStroke(1.dp, Color.Black),
            onClick = onClick
        ) {
            Text("Stop Exploring")
        }

        Spacer(modifier = Modifier.size(8.dp))

        // Give it a little crossfade so it doesn't abruptly enter/leave.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            var i = trackStartIndices.value[currentTrack?.id]
            val amountOfIntervals = currentAlbumTracks.size
            if (i != null) {
                while (i < amountOfIntervals && currentAlbumTracks[i].first == currentTrack) {
                    val interval = currentAlbumTracks[i].second

                    // For each interval we've got for the song (3 for > 45 seconds), create a duration card for it.
                    val duration = "${interval.first} - ${interval.second}"

                    Crossfade(
                        targetState = (i == currentIntervalIndex),
                        label = "Transition Active Interval"
                    ) { isSelected ->
                        val containerColor =
                            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

                        Card(
                            border = BorderStroke(1.dp, Color.Black),
                            colors = CardDefaults.cardColors(
                                containerColor = containerColor
                            ),
                            shape = RoundedCornerShape(0),
                        ) {
                            Text(
                                duration,
                                modifier = Modifier.padding(4.dp),
                                fontWeight = FontWeight(400)
                            )
                        }
                    }

                    i++
                }
            }
        }
    }
}

fun findFirstIndicesOfTracks(currentAlbumTracks: List<Pair<SimpleTrack, Pair<String, String>>>): Map<String, Int> {
    val seenTracks = mutableSetOf<String>()
    val firstIndices = mutableMapOf<String, Int>()

    currentAlbumTracks.forEachIndexed { index, (track, _) ->
        val trackId = track.id
        if (trackId !in seenTracks) {
            seenTracks.add(trackId)
            firstIndices[trackId] = index
        }
    }

    return firstIndices
}