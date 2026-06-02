package org.noztech.coppy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import org.noztech.coppy.core.MyActivityProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        MyActivityProvider.activity = this

        setContent {
            LaunchedEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
            }
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        MyActivityProvider.activity = this
    }

    override fun onPause() {
        MyActivityProvider.activity = null
        super.onPause()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
