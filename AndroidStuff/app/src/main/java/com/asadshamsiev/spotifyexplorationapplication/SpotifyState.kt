package com.asadshamsiev.spotifyexplorationapplication

import com.adamratzman.spotify.models.SimpleTrack

data class SpotifyState(
    val albumName: String,
    val currentAlbumTracks: ArrayList<Pair<ArrayList<Pair<String, String>>, SimpleTrack>>
)