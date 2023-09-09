package com.asadshamsiev.spotifyexplorationapplication

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.SimpleTrack
import com.spotify.android.appremote.api.SpotifyAppRemote

@Composable
fun TrackCard(spotifyAppRemote: SpotifyAppRemote? = null, track: SimpleTrack) {
    Card(
        border = BorderStroke(1.5.dp, Color.Black),
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                spotifyAppRemote?.playerApi?.play(track.uri.uri)
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
    currentAlbumTracks: MutableList<Any>
) {
    // If the shit isn't init.
    val tracksInit = currentAlbumTracks.size > 0
    if (currTrackName != "Track: " && tracksInit) {
        Column {
            Text(currAlbumName, fontSize = 12.sp)
            Text(currTrackName, fontSize = 12.sp)

            Spacer(modifier = Modifier.size(8.dp))

            // Something's not right here.
            if (currentAlbumTracks.size == 1 && currentAlbumTracks[0] is List<*>) {
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
        Text("Shit is loading, give it a second!")
    }
}

private fun msToDuration(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}