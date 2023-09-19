package com.asadshamsiev.spotifyexplorationapplication

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.SimpleTrack
import com.asadshamsiev.spotifyexplorationapplication.utils.TrackUtils
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote

@Composable
fun TrackListSection(
    spotifyAppRemote: SpotifyAppRemote? = null,
    viewModel: MainScreenViewModel
) {
    val currentAlbumTracks: ArrayList<Pair<ArrayList<Pair<String, String>>, SimpleTrack>> =
        viewModel.currentAlbumTracks.collectAsState().value
    val tracksInit = currentAlbumTracks.isNotEmpty()
    val exploreSessionStarted = remember { mutableStateOf(false) }

    val currentTrackIndex = remember { mutableStateOf(0) }
    val currentIntervalIndex = remember { mutableStateOf(0) }

    if (tracksInit) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // This button is the thing that actually starts the sampling.
            ExploreAlbumButton(
                currentAlbumTracks = currentAlbumTracks,
                currentIntervalIndex = currentIntervalIndex,
                currentTrackIndex = currentTrackIndex,
                exploreSessionStarted = exploreSessionStarted,
                spotifyAppRemote = spotifyAppRemote
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
                    for (pair in currentAlbumTracks) {
                        val castedPair = pair as? Pair<*, *>
                        val track = castedPair?.second

                        // For each track in the current album,
                        // create a TrackCard for it.
                        if (track is SimpleTrack) {
                            TrackCard(
                                currentIntervalIndex = currentIntervalIndex,
                                currentTrackIndex = currentTrackIndex,
                                exploreSessionStarted = exploreSessionStarted,
                                spotifyAppRemote = spotifyAppRemote,
                                track = track
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
        Text("Data is loading bruv.", fontStyle = FontStyle.Italic)
    }

    Spacer(modifier = Modifier.size(8.dp)) // A little space on the bottom.
}

@Composable
fun TrackCard(
    currentIntervalIndex: MutableState<Int>,
    currentTrackIndex: MutableState<Int>,
    exploreSessionStarted: MutableState<Boolean>,
    spotifyAppRemote: SpotifyAppRemote? = null,
    track: SimpleTrack
) {
    val isPlaying = remember { mutableStateOf(false) }

    LaunchedEffect(track) {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
            isPlaying.value = (state.track.uri == track.uri.uri)
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
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                Toast
                    .makeText(
                        context,
                        "Clicking a track will cause the explore session to end. Soz mate.",
                        Toast.LENGTH_SHORT
                    )
                    .show()

                try {
                    spotifyAppRemote?.playerApi?.play(track.uri.uri)
                    screwed.value = false
                } catch (e: Exception) {
                    Log.d("onClick", "Can't play specified song: $e")
                    screwed.value = true
                }

                // Clicking a track will interrupt an explore session.
                // Even if the remote API can't call it. Makes no difference. It will be false.
                exploreSessionStarted.value = false
                resetTrackRelatedIndices(currentTrackIndex, currentIntervalIndex)
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExploreAlbumButton(
    spotifyAppRemote: SpotifyAppRemote?,
    currentAlbumTracks: ArrayList<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>,
    exploreSessionStarted: MutableState<Boolean>,
    currentTrackIndex: MutableState<Int>,
    currentIntervalIndex: MutableState<Int>
) {
    val handler = rememberUpdatedState(Handler(Looper.getMainLooper()))
    val screwed = remember { mutableStateOf(false) }

    val checkProgressRunnable = object : Runnable {
        override fun run() {
            if (spotifyAppRemote != null && spotifyAppRemote.isConnected) {
                try {
                    spotifyAppRemote.playerApi.playerState?.setResultCallback { state ->
                        val currentPosition = state.playbackPosition
                        val currentTrackIntervals =
                            currentAlbumTracks[currentTrackIndex.value].first

                        val currentInterval = currentTrackIntervals[currentIntervalIndex.value]
                        val endOfCurrentInterval: Long =
                            TrackUtils.durationToMs((currentInterval.second))

                        // If we're past the interval, then move on to the next one.
                        if (currentPosition >= endOfCurrentInterval) {
                            currentIntervalIndex.value++

                            // If there's another interval to be played for this song, then play it.
                            if (currentIntervalIndex.value < currentTrackIntervals.size) {
                                val nextInterval = currentTrackIntervals[currentIntervalIndex.value]
                                val startOfNextInterval: Long =
                                    TrackUtils.durationToMs(nextInterval.first)

                                spotifyAppRemote.playerApi.seekTo(startOfNextInterval)
                            } else {
                                // Otherwise, move on to the next song and reset the interval index.
                                currentTrackIndex.value++
                                currentIntervalIndex.value = 0

                                if (currentTrackIndex.value < currentAlbumTracks.size) {
                                    spotifyAppRemote.playerApi.play(currentAlbumTracks[currentTrackIndex.value].second.uri.uri)

                                    val initialInterval =
                                        currentAlbumTracks[currentTrackIndex.value].first[0]
                                    val startOfFirstInterval: Long =
                                        TrackUtils.durationToMs(initialInterval.first)

                                    // Hot-fix. This shouldn't need to be here, cause I remove
                                    // the callable's, but it does, for some reason.
                                    // Need to read more about the internals on this.
                                    if (exploreSessionStarted.value) {
                                        handler.value.postDelayed({
                                            spotifyAppRemote.playerApi.seekTo(startOfFirstInterval)
                                        }, 500)
                                    }
                                } else {
                                    spotifyAppRemote.playerApi.pause()
                                    handler.value.removeCallbacks(this)
                                    return@setResultCallback
                                }
                            }
                        }

                        if (exploreSessionStarted.value) {
                            handler.value.postDelayed(this, 500)
                            screwed.value = false
                        }
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
        if (!exploreSessionStarted.value && remoteApiConnected) {
            spotifyAppRemote!!.playerApi.play(currentAlbumTracks[currentTrackIndex.value].second.uri.uri)
                ?.apply {
                    val initialInterval = currentAlbumTracks[currentTrackIndex.value].first[0]
                    val startOfFirstInterval = TrackUtils.durationToMs(initialInterval.first)

                    handler.value.postDelayed({
                        spotifyAppRemote.playerApi.seekTo(startOfFirstInterval)
                    }, 1000)

                    handler.value.post(checkProgressRunnable)
                }
            screwed.value = false
        } else if (remoteApiConnected) {
            spotifyAppRemote?.playerApi?.pause()
            // TODO: Fix this so the skipping stuff stops when no longer in exploration mode.
            handler.value.removeCallbacks(checkProgressRunnable)
            screwed.value = false
        } else {
            handler.value.removeCallbacks(checkProgressRunnable) // Just in case.
            Log.d("onClick", "Remote API not connected!")
            screwed.value = true
        }

        exploreSessionStarted.value = !exploreSessionStarted.value
    }

    LaunchedEffect(exploreSessionStarted.value) {
        if (!exploreSessionStarted.value) {
            try {
                handler.value.removeCallbacks(checkProgressRunnable)
                resetTrackRelatedIndices(currentTrackIndex, currentIntervalIndex)
            } catch (e: Exception) {
                Log.d("removeCallbacks", "Callback unsuccessfully removed: $e")
            }

            // Go back to the first song.
            val firstTrackInAlbum: SimpleTrack = currentAlbumTracks[0].second
            spotifyAppRemote?.playerApi?.play(firstTrackInAlbum.uri.uri)
            spotifyAppRemote?.playerApi?.pause()
        }
    }

    val currentTrackIntervals = currentAlbumTracks[currentTrackIndex.value].first

    if (!exploreSessionStarted.value) {
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
            // For each interval we've got for the song (3 as of now), create a card for it.
            for ((i, interval) in currentTrackIntervals.withIndex()) {
                val blurModifier = if (i != currentIntervalIndex.value) {
                    Modifier.blur(8.dp)
                } else {
                    Modifier
                }

                Card(
                    border = BorderStroke(0.5.dp, Color.Black),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(0)
                ) {
                    AnimatedContent(
                        targetState = blurModifier,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300, delayMillis = 300)) with
                                    fadeOut(animationSpec = tween(300, delayMillis = 0))
                        },
                        content = {
                            val duration = "${interval.first} - ${interval.second}"
                            Text(
                                duration, modifier = Modifier
                                    .padding(4.dp)
                                    .then(it)
                            )
                        }, label = "Update Interval Status"
                    )
                }
            }
        }
    }
}

private fun resetTrackRelatedIndices(
    currentTrackIndex: MutableState<Int>,
    currentIntervalIndex: MutableState<Int>
) {
    currentTrackIndex.value = 0
    currentIntervalIndex.value = 0
}