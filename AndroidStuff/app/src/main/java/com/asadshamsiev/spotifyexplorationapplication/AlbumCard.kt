package com.asadshamsiev.spotifyexplorationapplication

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

const val CARD_HEIGHT = 80
const val CARD_PADDING = 8

@Composable
fun AlbumCard(
    artistName: String,
    albumName: String,
    onClick: () -> Unit,
    link: String, // These'll eventually need defaults for if it craps out.
    modifier: Modifier = Modifier
) {
    val targetScale = remember { mutableStateOf(1f) }
    val scale: Float by animateFloatAsState(
        targetValue = targetScale.value,
        animationSpec = tween(durationMillis = 175, easing = FastOutSlowInEasing),
        label = "Shrink Album Card"
    )

    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        targetScale.value = 0.925f
                        // Capture the gesture until it's released.
                        val success = tryAwaitRelease()
                        // When released, scale back up to original size.
                        if (success) {
                            targetScale.value = 1f
                            onClick()
                        }
                    }
                )
            },
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