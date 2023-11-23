package com.asadshamsiev.spotifyexplorationapplication.tracklist.buttons

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asadshamsiev.spotifyexplorationapplication.utils.SimpleTrackWrapper

@Composable
fun DurationCards(
    trackStartIndices: State<Map<String, Int>>,
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    currentIntervalIndex: MutableState<Int>,
    currentTrack: SimpleTrackWrapper?
) {
    // Give it a little crossfade so it doesn't abruptly enter/leave.
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        var i = trackStartIndices.value[currentTrack?.track?.id]
        val amountOfIntervals = currentAlbumTracks.size
        if (i != null) {
            // Iterate until the next 3 tracks pretty much.
            while (i < amountOfIntervals && currentAlbumTracks[i].first == currentTrack) {
                val interval = currentAlbumTracks[i].second

                // For each interval we've got for the song (3 for > 45 seconds), create a duration card for it.
                val duration = "${interval.first} - ${interval.second}"

                Crossfade(
                    targetState = (i == currentIntervalIndex.value),
                    label = "Transition Active Interval"
                ) { isSelected ->
                    val containerColor =
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onSurface

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