package com.novacut.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.novacut.editor.presentation.navigation.AppNavHost
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioDarkBg

@UnstableApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NovaCutTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StudioDarkBg
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Preview(name = "Main Activity Preview", showBackground = true)
@Composable
fun MainActivityPreview() {
    NovaCutTheme {
        Box(modifier = Modifier.fillMaxSize().background(StudioDarkBg)) {
            AppNavHost()
        }
    }
}
