package com.asadshamsiev.spotifyexplorationapplication

data class SpotifyState(val albumName: String, val trackName: String, val currentAlbumTracks: List<Any>? = null)