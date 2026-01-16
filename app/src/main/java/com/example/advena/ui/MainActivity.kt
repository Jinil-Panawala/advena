package com.example.advena.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.advena.data.DbStorage
import com.example.advena.domain.Model
import com.example.advena.ui.navigation.AppNavigation
import com.example.advena.ui.theme.ADVENATheme

class MainActivity : ComponentActivity() {
    // Create single shared Model instance for the entire app

    private val model = Model(DbStorage())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock orientation to portrait to prevent layout issues
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableEdgeToEdge()
        setContent {
            ADVENATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass Model to AppNavigation
                    AppNavigation(model = model)
                }
            }
        }
    }
}