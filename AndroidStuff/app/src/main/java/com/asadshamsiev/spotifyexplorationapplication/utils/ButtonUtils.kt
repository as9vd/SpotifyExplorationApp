package com.asadshamsiev.spotifyexplorationapplication.utils

import android.os.Handler
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote

// TODO: Refactoring across the file to put a lot of functions into one common one.
fun getExploreButtonOnClickFunction(
    spotifyAppRemote: SpotifyAppRemote?,
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    viewModel: MainScreenViewModel,
    handler: State<Handler>,
    buttonClicked: MutableState<Boolean>,
    currentIntervalIndex: MutableState<Int>,
    checkProgressRunnable: Runnable
): () -> Unit {
    return {
        val remoteApiConnected = (spotifyAppRemote != null && spotifyAppRemote.isConnected)
        if (!viewModel.isExploreSessionStarted && remoteApiConnected) {
            // Reset the index. Will start from the beginning, at the top of the list.
            viewModel.currentIntervalIndex.value = 0

            // Get the first track and its uri, because we'll play it.
            val firstTrack = currentAlbumTracks[0].first
            val firstTrackUri = firstTrack.track.uri.uri

            spotifyAppRemote!!.playerApi.play(firstTrackUri)
                ?.apply {
                    val initialInterval = currentAlbumTracks[currentIntervalIndex.value].second
                    val startOfFirstInterval = TrackUtils.durationToMs(initialInterval.first)

                    handler.value.postDelayed({
                        spotifyAppRemote.playerApi.seekTo(startOfFirstInterval)
                    }, 500)

                    handler.value.post(checkProgressRunnable)

                    // If you can load it, then good.
                    viewModel.isLocalSpotifyDead = false
                }?.setErrorCallback {
                    // If you can't play this, then the local API is screwed.
                    viewModel.isLocalSpotifyDead = true
                }
            buttonClicked.value = true
        } else if (remoteApiConnected) {
            spotifyAppRemote?.playerApi?.pause()

            handler.value.removeCallbacks(checkProgressRunnable)
            buttonClicked.value = false

            viewModel.isLocalSpotifyDead = false
        } else {
            handler.value.removeCallbacks(checkProgressRunnable) // Just in case.
            Log.d("onClick", "Remote API not connected!")

            viewModel.isLocalSpotifyDead = true
        }

        viewModel.setIsExploreSessionStarted(!viewModel.isExploreSessionStarted)
    }
}

fun getExploreProgressRunnable(
    spotifyAppRemote: SpotifyAppRemote?,
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    currentIntervalIndex: MutableState<Int>,
    viewModel: MainScreenViewModel,
    handler: State<Handler>
): Runnable {
    return object : Runnable {
        override fun run() {
            if (spotifyAppRemote != null && spotifyAppRemote.isConnected) {
                try {
                    spotifyAppRemote.playerApi.playerState?.setResultCallback { state ->
                        val currentPosition = state.playbackPosition

                        // This is the paired interval (e.g. <"1:28", "2:56">).
                        val currentInterval =
                            currentAlbumTracks[currentIntervalIndex.value].second

                        val endOfCurrentInterval: Long =
                            TrackUtils.durationToMs(currentInterval.second)

                        // If we're past the interval, then move on to the next one.
                        if (currentPosition >= endOfCurrentInterval) {
                            viewModel.currentIntervalIndex.value = currentIntervalIndex.value + 1

                            // If there's another interval to be played, then play it.
                            val amountOfIntervals: Int = currentAlbumTracks.size
                            val isAtEnd: Boolean = currentIntervalIndex.value >= amountOfIntervals
                            if (!isAtEnd) {
                                val trackToBePlayed =
                                    currentAlbumTracks[currentIntervalIndex.value].first
                                val previousTrackPlayed =
                                    currentAlbumTracks[currentIntervalIndex.value - 1].first
                                val nextInterval =
                                    currentAlbumTracks[currentIntervalIndex.value].second
                                val startOfNextInterval: Long =
                                    TrackUtils.durationToMs(nextInterval.first)

                                if (trackToBePlayed == previousTrackPlayed) {
                                    spotifyAppRemote.playerApi.seekTo(startOfNextInterval)
                                } else {
                                    // First interval starts at 0:00 (in this implementation), so
                                    // no need to skip now.
                                    spotifyAppRemote.playerApi.play(trackToBePlayed.track.uri.uri)
                                        .setErrorCallback {
                                            Log.d(
                                                "errorCallback",
                                                "Couldn't seek to the start of the next for the new song."
                                            )
                                        }
                                }
                            } else {
                                // If you're at the end (e.g. there are no more tracks), just
                                // stop the exploration process.
                                spotifyAppRemote.playerApi.pause()

                                // To reset. Can explore again afterward.
                                viewModel.setIsExploreSessionStarted(false)

                                handler.value.removeCallbacks(this)
                                return@setResultCallback
                            }

                        }


                        if (viewModel.isExploreSessionStarted) {
                            handler.value.postDelayed(this, 500)
                        }
                    }?.setErrorCallback {
                        Log.d("it", it.toString())
                    }
                } catch (e: Exception) {
                    Log.d("checkProgressRunnable", "checkProgressRunnable failed: $e")
                }
            } else {
                Log.d(
                    "checkProgressRunnable",
                    "checkProgressRunnable failed, as SpotifyAppRemote is either null or not connected."
                )
            }
        }
    }
}

fun findFirstIndicesOfTracks(
    currentAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>
): Map<String, Int> {
    val seenTracks = mutableSetOf<String>()
    val firstIndices = mutableMapOf<String, Int>()

    currentAlbumTracks.forEachIndexed { index, (track, _) ->
        val trackId = track.track.id
        if (trackId !in seenTracks) {
            seenTracks.add(trackId)
            firstIndices[trackId] = index
        }
    }

    return firstIndices
}

fun getSpeedButtonOnClickFunction(
    spotifyAppRemote: SpotifyAppRemote?,
    currentSpeedAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    viewModel: MainScreenViewModel,
    handler: State<Handler>,
    buttonClicked: MutableState<Boolean>,
    currentIntervalIndex: MutableState<Int>,
    checkProgressRunnable: Runnable
): () -> Unit {
    return {
        val remoteApiConnected = (spotifyAppRemote != null && spotifyAppRemote.isConnected)
        if (!viewModel.isSpeedSessionStarted && remoteApiConnected) {
            // Reset the index. Will start from the beginning, at the top of the list.
            viewModel.currentIntervalIndex.value = 0

            // Get the first track and its uri, because we'll play it.
            val firstTrack = currentSpeedAlbumTracks[0].first
            val firstTrackUri = firstTrack.track.uri.uri

            spotifyAppRemote!!.playerApi.play(firstTrackUri)
                ?.apply {
                    val initialInterval = currentSpeedAlbumTracks[currentIntervalIndex.value].second
                    val startOfFirstInterval = TrackUtils.durationToMs(initialInterval.first)

                    handler.value.postDelayed({
                        spotifyAppRemote.playerApi.seekTo(startOfFirstInterval)
                    }, 500)

                    handler.value.post(checkProgressRunnable)

                    // If you can load it, then good.
                    viewModel.isLocalSpotifyDead = false
                }?.setErrorCallback {
                    // If you can't play this, then the local API is screwed.
                    viewModel.isLocalSpotifyDead = true
                }
            buttonClicked.value = true
        } else if (remoteApiConnected) {
            spotifyAppRemote?.playerApi?.pause()

            handler.value.removeCallbacks(checkProgressRunnable)
            buttonClicked.value = false

            viewModel.isLocalSpotifyDead = false
        } else {
            handler.value.removeCallbacks(checkProgressRunnable) // Just in case.
            Log.d("onClick", "Remote API not connected!")

            viewModel.isLocalSpotifyDead = true
        }

        viewModel.setIsSpeedSessionStarted(!viewModel.isSpeedSessionStarted)
    }
}

fun getSpeedProgressRunnable(
    spotifyAppRemote: SpotifyAppRemote?,
    currentSpeedAlbumTracks: List<Pair<SimpleTrackWrapper, Pair<String, String>>>,
    currentIntervalIndex: MutableState<Int>,
    viewModel: MainScreenViewModel,
    handler: State<Handler>
): Runnable {
    return object : Runnable {
        override fun run() {
            if (spotifyAppRemote != null && spotifyAppRemote.isConnected) {
                try {
                    spotifyAppRemote.playerApi.playerState?.setResultCallback { state ->
                        val currentPosition = state.playbackPosition

                        // This is the paired interval (e.g. <"1:28", "2:56">).
                        val currentInterval =
                            currentSpeedAlbumTracks[currentIntervalIndex.value].second

                        val endOfCurrentInterval: Long =
                            TrackUtils.durationToMs(currentInterval.second)

                        // If we're past the interval, then move on to the next one.
                        if (currentPosition >= endOfCurrentInterval) {
                            viewModel.currentIntervalIndex.value = currentIntervalIndex.value + 1

                            // If there's another interval to be played, then play it.
                            val amountOfIntervals: Int = currentSpeedAlbumTracks.size
                            val isAtEnd: Boolean = currentIntervalIndex.value >= amountOfIntervals
                            if (!isAtEnd) {
                                val trackToBePlayed =
                                    currentSpeedAlbumTracks[currentIntervalIndex.value].first
                                val previousTrackPlayed =
                                    currentSpeedAlbumTracks[currentIntervalIndex.value - 1].first
                                val nextInterval =
                                    currentSpeedAlbumTracks[currentIntervalIndex.value].second
                                val startOfNextInterval: Long =
                                    TrackUtils.durationToMs(nextInterval.first)

                                if (trackToBePlayed == previousTrackPlayed) {
                                    spotifyAppRemote.playerApi.seekTo(startOfNextInterval)
                                } else {
                                    // First interval starts at 0:00 (in this implementation), so
                                    // no need to skip now.
                                    spotifyAppRemote.playerApi.play(trackToBePlayed.track.uri.uri)
                                        .setErrorCallback {
                                            Log.d(
                                                "errorCallback",
                                                "Couldn't seek to the start of the next for the new song."
                                            )
                                        }
                                }
                            } else {
                                // If you're at the end (e.g. there are no more tracks), just
                                // stop the exploration process.
                                spotifyAppRemote.playerApi.pause()

                                // To reset. Can speed again afterward.
                                viewModel.setIsSpeedSessionStarted(false)

                                handler.value.removeCallbacks(this)
                                return@setResultCallback
                            }
                        }


                        if (viewModel.isSpeedSessionStarted) {
                            handler.value.postDelayed(this, 500)
                        }
                    }?.setErrorCallback {
                        Log.d("it", it.toString())
                    }
                } catch (e: Exception) {
                    Log.d("checkProgressRunnable", "checkProgressRunnable failed: $e")
                }
            } else {
                Log.d(
                    "checkProgressRunnable",
                    "checkProgressRunnable failed, as SpotifyAppRemote is either null or not connected."
                )
            }
        }
    }
}