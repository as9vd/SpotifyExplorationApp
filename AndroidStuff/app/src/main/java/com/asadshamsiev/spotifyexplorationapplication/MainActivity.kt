package com.asadshamsiev.spotifyexplorationapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.asadshamsiev.spotifyexplorationapplication.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Column {
                    Text("Start a Session")
                    Button(onClick = {}) {
                        Text("Click to start typing")
                    }
                }
            }
        }
    }
}