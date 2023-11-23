package com.asadshamsiev.spotifyexplorationapplication.tracklist.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.asadshamsiev.spotifyexplorationapplication.viewmodels.MainScreenViewModel

@Composable
fun ButtonSection(
    viewModel: MainScreenViewModel,
    currentIntervalIndex: MutableState<Int>,
    isLoadingTracks: Boolean
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExploreAlbumButton(
                viewModel = viewModel,
                currentIntervalIndex = currentIntervalIndex,
                isLoading = isLoadingTracks
            )

            // TODO: Speed mode button.
            Button(onClick = {}) {
                Text(text = "Speed")
            }
        }
    }
}
