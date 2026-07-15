package com.example.questomaniato_doapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.questomaniato_doapp.ui.screens.QuestScreen
import com.example.questomaniato_doapp.ui.screens.SplashScreen
import com.example.questomaniato_doapp.ui.theme.QuestomaniaTheme
import com.example.questomaniato_doapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestomaniaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var showSplash by remember { mutableStateOf(true) }

                    // Auto-transition from splash to main after 2.5 s
                    LaunchedEffect(Unit) {
                        delay(2500)
                        showSplash = false
                    }

                    Crossfade(
                        targetState = showSplash,
                        animationSpec = tween(durationMillis = 600)
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen()
                        } else {
                            val viewModel: MainViewModel = viewModel()
                            QuestScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
