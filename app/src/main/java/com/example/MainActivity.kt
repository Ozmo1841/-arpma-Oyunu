package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MultiplicationViewModel
import com.example.ui.components.GameOverDialog
import com.example.ui.components.HistoryDialog
import com.example.ui.components.RulesDialog
import com.example.ui.screens.GameScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MultiplicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MultiplicationApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MultiplicationApp(viewModel: MultiplicationViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val highScore by viewModel.allTimeHighScore.collectAsStateWithLifecycle()
    val history by viewModel.gameHistory.collectAsStateWithLifecycle()

    var showRulesDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    // Intercept back press when game is active
    BackHandler(enabled = uiState.isGameActive) {
        viewModel.stopGameEarly()
    }

    if (uiState.isGameActive) {
        GameScreen(
            state = uiState,
            onDigitClick = { digit -> viewModel.appendDigit(digit) },
            onDeleteClick = { viewModel.deleteLastDigit() },
            onClearClick = { viewModel.clearInput() },
            onSubmitClick = { viewModel.submitAnswer() },
            onPauseResume = { viewModel.pauseResumeGame() },
            onQuit = { viewModel.stopGameEarly() }
        )
    } else {
        SetupScreen(
            state = uiState,
            highScore = highScore ?: 0,
            availableDurations = viewModel.availableDurations,
            onSelectDuration = { dur -> viewModel.setDuration(dur) },
            onToggleNumber = { num -> viewModel.toggleNumber(num) },
            onSelectPreset = { preset -> viewModel.selectNumberPreset(preset) },
            onSelectGameMode = { mode -> viewModel.setGameMode(mode) },
            onSelectBotDifficulty = { diff -> viewModel.setBotDifficulty(diff) },
            onToggleAutoSubmit = { viewModel.toggleAutoSubmit() },
            onStartGame = { viewModel.startGame() },
            onOpenRules = { showRulesDialog = true },
            onOpenHistory = { showHistoryDialog = true }
        )
    }

    // Game Over Results Dialog
    if (uiState.isGameOver) {
        GameOverDialog(
            state = uiState,
            onRestart = {
                viewModel.dismissGameOver()
                viewModel.startGame()
            },
            onDismissToSettings = {
                viewModel.dismissGameOver()
            }
        )
    }

    // Rules & Scoring Guide Dialog
    if (showRulesDialog) {
        RulesDialog(onDismiss = { showRulesDialog = false })
    }

    // Past History Dialog
    if (showHistoryDialog) {
        HistoryDialog(
            records = history,
            onClearHistory = { viewModel.clearScores() },
            onDismiss = { showHistoryDialog = false }
        )
    }
}
