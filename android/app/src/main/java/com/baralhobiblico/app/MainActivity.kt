package com.baralhobiblico.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baralhobiblico.app.game.GameViewModel
import com.baralhobiblico.app.game.Screen
import com.baralhobiblico.app.ui.screens.*
import com.baralhobiblico.app.ui.theme.Bg
import com.baralhobiblico.app.ui.theme.BaralhoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BaralhoTheme { AppRoot() }
        }
    }
}

@Composable
fun AppRoot(vm: GameViewModel = viewModel()) {
    Surface(color = Bg, modifier = Modifier.fillMaxSize()) {
        when (vm.screen) {
            Screen.HOME -> HomeScreen(vm)
            Screen.SETUP -> SetupScreen(vm)
            Screen.GAME -> GameScreen(vm)
            Screen.RESULT -> ResultScreen(vm)
            Screen.STUDY -> StudyScreen(vm)
            Screen.STUDY_CARD -> StudyCardScreen(vm)
        }
    }
}
