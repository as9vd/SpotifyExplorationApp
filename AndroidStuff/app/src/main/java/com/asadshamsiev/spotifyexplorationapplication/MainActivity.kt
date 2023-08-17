package com.asadshamsiev.spotifyexplorationapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asadshamsiev.spotifyexplorationapplication.ui.theme.AppTheme
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

class MainActivity : ComponentActivity() {
    private val clientId = "d6d33d89d3a044618291f268d1eea409"
    private val redirectUri = "com.asadshamsiev.spotifyexplorationapplication://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onStart() {
        super.onStart()

        // Set the connection parameters.
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("SpotifyStuff", "Connected! Finally.")
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyStuff", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })

        // authenticateSpotify()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            SoLoader.init(this, false)
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(applicationContext, DescriptorMapping.withDefaults()))
            client.start()
        }

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    private fun authenticateSpotify() {
        val builder = AuthorizationRequest.Builder(
            "d6d33d89d3a044618291f268d1eea409",
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )

        builder.setScopes(listOf("streaming").toTypedArray())
        val request: AuthorizationRequest = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if result comes from the correct activity.
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d("SpotifyStuff", "Thank goodness.")
                }
                AuthorizationResponse.Type.ERROR -> {}
                AuthorizationResponse.Type.EMPTY -> {
                    Log.d("SpotifyStuff", "It's empty.")
                }
                else -> {}
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun MainScreen() {
        val textFieldQuery = remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Start a Session")
            TextField(
                value = textFieldQuery.value,
                placeholder = {
                    Text("Click to start typing")
                },
                onValueChange = {
                    textFieldQuery.value = it
                }
            )
            Button(onClick = {
                spotifyAppRemote?.playerApi?.play("spotify:track:01Lr5YepbgjXAWR9iOEyH1")
            }) {
                Text("Click to play Love Sosa")
            }
        }
    }
}