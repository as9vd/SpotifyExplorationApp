package com.asadshamsiev.spotifyexplorationapplication

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.SimpleTrack
import com.spotify.android.appremote.api.SpotifyAppRemote

val trackUtils = TrackUtils()

@Composable
fun TrackCard(spotifyAppRemote: SpotifyAppRemote? = null, track: SimpleTrack) {
    val context = LocalContext.current

    val intervals = remember { trackUtils.sampleSong(track.length) }
    var currentIndex = remember { mutableStateOf(0) }
    var currentInterval = remember { mutableStateOf(intervals[currentIndex.value]) }

    val handler = rememberUpdatedState(Handler(Looper.getMainLooper()))
    val checkProgressRunnable = object : Runnable {
        override fun run() {
            spotifyAppRemote?.playerApi?.playerState?.setResultCallback { state ->
                val currentPosition = state.playbackPosition

                if (currentPosition >= trackUtils.durationToMs(currentInterval.value.second)) {
                    currentIndex.value++

                    if (currentIndex.value < intervals.size) {
                        currentInterval.value = intervals[currentIndex.value]
                        spotifyAppRemote.playerApi.seekTo(trackUtils.durationToMs(currentInterval.value.first))
                    } else {
                        spotifyAppRemote.playerApi.pause()
                        handler.value.removeCallbacks(this)
                        return@setResultCallback
                    }
                }

                handler.value.postDelayed(this, 500) // Every half second.
            }
        }
    }

    val onClick: () -> Unit = {
        spotifyAppRemote?.playerApi?.play(track.uri.uri)?.setResultCallback {
            // Once play has succeeded, then you can seek.
            spotifyAppRemote.playerApi.seekTo(
                trackUtils.durationToMs(
                    currentInterval.value.first
                )
            )?.setResultCallback {
                Log.d("PositionSought", "Position successfully sought mate (I think?).")
            }?.setErrorCallback { throwable ->
                Toast.makeText(
                    context,
                    "Position wasn't successfully sought you muppet: ${throwable.message}.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("PositionSought", "Screwed up: ${throwable.message}")
            }

            handler.value.post(checkProgressRunnable) // Check progress as it cracks on mate.

            Toast.makeText(
                context,
                "Curr: ${currentInterval.value} Translated: ${
                    (trackUtils.durationToMs(
                        currentInterval.value.first
                    ))
                } " + intervals.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Card(
        border = BorderStroke(1.5.dp, Color.Black),
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                "${track.trackNumber}. ${track.name} (${
                    trackUtils.msToDuration(
                        track.length
                    )
                })",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun TrackListCards(
    spotifyAppRemote: SpotifyAppRemote? = null,
    currTrackName: String,
    currAlbumName: String,
    currAlbumUri: String,
    currentAlbumTracks: List<Any>
) {
    val tracksInit = currentAlbumTracks.isNotEmpty()
    val context = LocalContext.current

    if (currTrackName != "Track: " && tracksInit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(currAlbumName, fontSize = 12.sp, textAlign = TextAlign.Start)
            Text(currTrackName, fontSize = 12.sp, textAlign = TextAlign.Start)

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = {
                    try {
                        spotifyAppRemote?.playerApi?.queue(currAlbumUri) // This shit doesn't work.
                        Toast.makeText(context, "Album queued mate.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.d("Queueing", "Failed to queue.")
                    }
                }
            ) {
                Text("Add All to Queue")
            }

            Spacer(modifier = Modifier.size(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(2.5.dp)
            ) {
                for (pair in currentAlbumTracks) {
                    val castedPair = pair as? Pair<*, *>

                    val duration = castedPair?.first
                    val track = castedPair?.second

                    if (track is SimpleTrack) {
                        TrackCard(spotifyAppRemote = spotifyAppRemote, track = track)
                    }
                }
            }
        }
    } else {
        CircularProgressIndicator()
        Text("Data is loading, give it a second!")
    }
}

