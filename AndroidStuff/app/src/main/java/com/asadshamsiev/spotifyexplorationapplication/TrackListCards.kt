package com.asadshamsiev.spotifyexplorationapplication

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.SimpleTrack
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlin.random.Random

@Composable
fun TrackCard(spotifyAppRemote: SpotifyAppRemote? = null, track: SimpleTrack) {
    val context = LocalContext.current
    Card(
        border = BorderStroke(1.5.dp, Color.Black),
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                spotifyAppRemote?.playerApi?.play(track.uri.uri)
                Toast.makeText(context, sampleSong(track.length), Toast.LENGTH_SHORT).show()
            }
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                "${track.trackNumber}. ${track.name} (${
                    msToDuration(
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
    currentAlbumTracks: List<Any>
) {
    val tracksInit = currentAlbumTracks.isNotEmpty()

    if (currTrackName != "Track: " && tracksInit) {
        Column {
            if (currentAlbumTracks.size == 1 && currentAlbumTracks[0] is List<*>) {
                Text(currAlbumName, fontSize = 12.sp, textAlign = TextAlign.Start)
                Text(currTrackName, fontSize = 12.sp, textAlign = TextAlign.Start)

                Spacer(modifier = Modifier.size(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                    for (track in (currentAlbumTracks[0] as List<*>)) {
                        if (track is SimpleTrack) {
                            TrackCard(spotifyAppRemote = spotifyAppRemote, track = track)
                        }
                    }
                }
            }

        }
    } else {
        CircularProgressIndicator()
        Text("Data is loading, give it a second!")
    }
}

data class Period(val start: Int, val end: Int) {
    val length: Int get() = end - start
}

private fun msToDuration(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

fun sampleSong(totalLengthMillis: Int): String {
    val targetLength = (totalLengthMillis * 0.51).toInt()
    val numPeriods = Random.nextInt(4, 8)

    val periods = mutableListOf<Period>()

    var currentLength = 0
    var lastEnd = 0

    for (i in 0 until numPeriods - 1) {
        val remainingPeriods = numPeriods - periods.size
        val remainingLength = targetLength - currentLength
        val averageRemainingLength = remainingLength / remainingPeriods

        val maxStart = totalLengthMillis - averageRemainingLength - (remainingPeriods - 1) * averageRemainingLength
        val start = Random.nextInt(lastEnd, maxStart.coerceAtLeast(lastEnd))
        val end = start + averageRemainingLength

        periods.add(Period(start, end))
        currentLength += averageRemainingLength
        lastEnd = end
    }

    // Adjust last period to match target length
    val lastPeriodStart = Random.nextInt(lastEnd, totalLengthMillis - (targetLength - currentLength))
    periods.add(Period(lastPeriodStart, lastPeriodStart + (targetLength - currentLength)))

    var return_val: String = ""
    periods.forEach { period ->
        return_val += "${msToDuration(period.start)} - ${msToDuration(period.end)} "
    }

    return return_val
}