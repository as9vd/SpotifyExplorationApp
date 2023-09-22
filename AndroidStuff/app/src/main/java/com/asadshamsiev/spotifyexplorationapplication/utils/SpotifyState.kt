package com.asadshamsiev.spotifyexplorationapplication.utils

import androidx.compose.runtime.Stable
import com.adamratzman.spotify.models.SimpleTrack

data class SpotifyState(
    val albumName: String,
    val currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>
)

@Stable
data class SimpleTrackWrapper(
    val track: SimpleTrack
)