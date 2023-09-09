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

val trackUtils = TrackUtils()

@Composable
fun TrackCard(spotifyAppRemote: SpotifyAppRemote? = null, track: SimpleTrack) {
    val context = LocalContext.current
    Card(
        border = BorderStroke(1.5.dp, Color.Black),
        shape = RoundedCornerShape(0), modifier = Modifier
            .clickable {
                spotifyAppRemote?.playerApi?.play(track.uri.uri)
                Toast
                    .makeText(
                        context,
                        trackUtils
                            .sampleSong(track.length)
                            .toString(),
                        Toast.LENGTH_SHORT
                    )
                    .show()
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
    currentAlbumTracks: List<Any>
) {
    val tracksInit = currentAlbumTracks.isNotEmpty()

    if (currTrackName != "Track: " && tracksInit) {
        Column {

            Text(currAlbumName, fontSize = 12.sp, textAlign = TextAlign.Start)
            Text(currTrackName, fontSize = 12.sp, textAlign = TextAlign.Start)

            Spacer(modifier = Modifier.size(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(2.5.dp)
            ) {
                for (pair in currentAlbumTracks) {
                    val castedPair = pair as? Pair<*, *>
                    val duration = castedPair?.first
                    val track = castedPair?.second
                    if (track is SimpleTrack) {
                        Text(duration.toString())
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

