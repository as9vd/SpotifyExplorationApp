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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.SimpleTrack
import com.spotify.android.appremote.api.SpotifyAppRemote

val trackUtils = TrackUtils()

@Composable
fun TrackCard(
    spotifyAppRemote: SpotifyAppRemote? = null,
    track: SimpleTrack,
    active: Boolean
) {
    val context = LocalContext.current
    val isPlaying = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
            isPlaying.value =
                if (state.track.uri == track.uri.uri) {
                    true
                } else {
                    false
                }
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val shake by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(75, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        border = BorderStroke(1.5.dp, Color.Black),
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                Toast
                    .makeText(context, "Tee-hee!", Toast.LENGTH_SHORT)
                    .show()
            }
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Crossfade(targetState = isPlaying.value) { playing ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${track.trackNumber}.",
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.15f),
                        textAlign = TextAlign.Center
                    )

                    if (playing) {
                        Text(
                            "🦴",
                            modifier = Modifier.graphicsLayer(rotationZ = shake)
                        )
                        Text(
                            "${track.name} (${trackUtils.msToDuration(track.length)})",
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(0.85f)
                        )
                    } else {
                        Text(
                            "${track.name} (${trackUtils.msToDuration(track.length)})",
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(0.85f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExploreAlbumButton(
    spotifyAppRemote: SpotifyAppRemote,
    currentAlbumTracks: ArrayList<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>,
    currAlbumUri: String,
    albumChanged: Boolean
) {
    val buttonClicked = remember { mutableStateOf(false) }
    var currentTrackIndex = remember { mutableStateOf(0) }
    var currentIntervalIndex = remember { mutableStateOf(0) }

    val handler = rememberUpdatedState(Handler(Looper.getMainLooper()))

    val checkProgressRunnable = object : Runnable {
        override fun run() {
            spotifyAppRemote.playerApi.playerState?.setResultCallback { state ->
                val currentPosition = state.playbackPosition

                val currentTrack = currentAlbumTracks[currentTrackIndex.value].second
                val currentTrackIntervals = currentAlbumTracks[currentTrackIndex.value].first

                val currentInterval = currentTrackIntervals[currentIntervalIndex.value]
                val endOfCurrentInterval: Long = trackUtils.durationToMs((currentInterval.second))

                if (currentPosition >= endOfCurrentInterval) {
                    currentIntervalIndex.value++

                    // If there's another interval to be played for this song, then play it.
                    if (currentIntervalIndex.value < currentTrackIntervals.size) {
                        val nextInterval = currentTrackIntervals[currentIntervalIndex.value]
                        val startOfNextInterval: Long = trackUtils.durationToMs(nextInterval.first)

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
                                trackUtils.durationToMs(initialInterval.first)

                            handler.value.postDelayed({
                                spotifyAppRemote.playerApi.seekTo(startOfFirstInterval)
                            }, 1000)
                        } else {
                            spotifyAppRemote.playerApi.pause()
                            handler.value.removeCallbacks(this)
                            return@setResultCallback
                        }
                    }
                }
                handler.value.postDelayed(this, 1000)
            }
        }
    }

    val onClick: () -> Unit = {
        if (!buttonClicked.value) {
            spotifyAppRemote.playerApi.play(currentAlbumTracks[currentTrackIndex.value].second.uri.uri)
                ?.apply {
                    val initialInterval = currentAlbumTracks[currentTrackIndex.value].first[0]
                    val startOfFirstInterval = trackUtils.durationToMs(initialInterval.first)

                    handler.value.postDelayed({
                        spotifyAppRemote.playerApi.seekTo(startOfFirstInterval)
                    }, 1000)

                    handler.value.post(checkProgressRunnable)
                }
        } else {
            spotifyAppRemote.playerApi.pause()
            handler.value.removeCallbacks(checkProgressRunnable)
        }

        buttonClicked.value = !buttonClicked.value
    }

    val currentTrackIntervals = currentAlbumTracks[currentTrackIndex.value].first
    val currentInterval = currentTrackIntervals[currentIntervalIndex.value]

    if (!buttonClicked.value) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrackListCards(
    spotifyAppRemote: SpotifyAppRemote? = null,
    currAlbumName: String,
    currAlbumUri: String,
    currentAlbumTracks: ArrayList<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>,
    changed: Boolean
) {
    val tracksInit = currentAlbumTracks.isNotEmpty()

    if (tracksInit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExploreAlbumButton(
                spotifyAppRemote = spotifyAppRemote!!,
                currentAlbumTracks = currentAlbumTracks,
                currAlbumUri = currAlbumUri,
                albumChanged = changed
            )

            Spacer(modifier = Modifier.size(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for ((index, pair) in currentAlbumTracks.withIndex()) {
                    val castedPair = pair as? Pair<*, *>

                    val duration = castedPair?.first
                    val track = castedPair?.second

                    if (track is SimpleTrack) {
                        TrackCard(
                            spotifyAppRemote = spotifyAppRemote,
                            track = track,
                            active = false
                        )
                    }
                }
            }
        }
    } else {
        CircularProgressIndicator()
        Text("Data is loading bruv.", fontStyle = FontStyle.Italic)
    }
}

