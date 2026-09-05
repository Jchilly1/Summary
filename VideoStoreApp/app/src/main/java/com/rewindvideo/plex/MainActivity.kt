package com.rewindvideo.plex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rewindvideo.plex.navigation.RewindNavHost
import com.rewindvideo.plex.ui.theme.RewindVideoClubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RewindVideoClubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RewindNavHost()
                }
            }
        }
    }
}
