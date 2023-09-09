package com.asadshamsiev.spotifyexplorationapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

const val CARD_HEIGHT = 80
const val CARD_PADDING = 8

@Composable
fun AlbumCard(
    artistName: String,
    albumName: String,
    link: String, // These'll eventually need defaults for if it craps out.
    modifier: Modifier = Modifier
) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        shape = RoundedCornerShape(0),
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp)
                .height(CARD_HEIGHT.dp)
                .fillMaxWidth()
                .padding(CARD_PADDING.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = link,
                contentDescription = null,
                modifier = Modifier
                    .width(64.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black
                    )
            )
            Spacer(Modifier.size(16.dp))
            Column {
                Text(artistName, Modifier.width(200.dp), fontSize = 16.sp, lineHeight = 12.sp)
                Text(albumName, Modifier.width(200.dp), fontSize = 12.sp, lineHeight = 12.sp)
            }
        }
    }
}