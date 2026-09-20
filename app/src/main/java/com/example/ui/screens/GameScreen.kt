package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AnswerFeedback
import com.example.ui.GameMode
import com.example.ui.GameUiState
import com.example.ui.components.FastKeypad
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.MathAmber
import com.example.ui.theme.MathCyan
import com.example.ui.theme.MathEmerald
import com.example.ui.theme.MathIndigo
import com.example.ui.theme.MathRose

@Composable
fun GameScreen(
    state: GameUiState,
    onDigitClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onPauseResume: () -> Unit,
    onQuit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar with Timer & Controls
            GameTopBar(
                remainingSeconds = state.remainingSeconds,
                totalDuration = state.selectedDurationSeconds,
                isPaused = state.isPaused,
                onPauseResume = onPauseResume,
                onQuit = onQuit
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Mode specific status widget (VS Bot duel or Solo multiplier)
            if (state.gameMode == GameMode.VS_BOT) {
                VsBotHeader(state = state)
            } else {
                SoloHeader(state = state)
            }

            // Milestone Banner
            AnimatedVisibility(
                visible = state.milestoneEvent != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = MathAmber,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = state.milestoneEvent.orEmpty(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Main Question Card
            QuestionCard(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.weight(0.6f))

            // Fast Keypad
            FastKeypad(
                onDigitClick = onDigitClick,
                onDeleteClick = onDeleteClick,
                onClearClick = onClearClick,
                onSubmitClick = onSubmitClick
            )
        }

        // Pause Overlay
        if (state.isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OYUN DURAKLATILDI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        IconButton(
                            onClick = onPauseResume,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MathIndigo)
                        ) {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "Devam Et",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Devam etmek için dokun",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameTopBar(
    remainingSeconds: Int,
    totalDuration: Int,
    isPaused: Boolean,
    onPauseResume: () -> Unit,
    onQuit: () -> Unit
) {
    val progress = (remainingSeconds.toFloat() / totalDuration).coerceIn(0f, 1f)
    val timerColor = if (remainingSeconds <= 10) MathRose else MathIndigo

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onQuit,
            modifier = Modifier.testTag("game_btn_quit")
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Oyundan Çık", tint = MaterialTheme.colorScheme.onSurface)
        }

        // Circular Countdown Display
        Box(
            modifier = Modifier.size(62.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = timerColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 5.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$remainingSeconds",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = timerColor
                )
                Text(
                    text = "sn",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(
            onClick = onPauseResume,
            modifier = Modifier.testTag("game_btn_pause")
        ) {
            Icon(
                imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                contentDescription = if (isPaused) "Devam Et" else "Duraklat",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SoloHeader(state: GameUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score & Star Tier
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${state.score}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = MathIndigo
                        )
                        Text(
                            text = " Puan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StarRatingRow(
                        stars = state.currentStars,
                        maxStars = 3,
                        starSize = 16
                    )
                }

                // Multiplier Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (state.multiplier) {
                        5 -> MathRose
                        4 -> MathAmber
                        3 -> MathCyan
                        2 -> MathIndigo
                        else -> MaterialTheme.colorScheme.surface
                    },
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (state.multiplier > 1) Color.White else MathAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "x${state.multiplier} ${if (state.multiplier >= 5) "MAX" else "ÇARPAN"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (state.multiplier > 1) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Multiplier progress bar
            val progress = state.progressToNextMultiplier
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MathAmber,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Seri: ${state.currentStreak} soru",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (state.multiplier >= 5) "Maksimum Çarpan!" else "x${state.multiplier + 1} için: ${state.streakNeededForNextMultiplier} doğru",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MathAmber
                )
            }
        }
    }
}

@Composable
private fun VsBotHeader(state: GameUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Side
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "SEN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MathIndigo
                    )
                    Text(
                        text = "${state.score}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "x${state.multiplier} (${state.currentStreak} seri)",
                        fontSize = 11.sp,
                        color = MathAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // VS Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MathAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = MathAmber
                    )
                }

                // Bot Side
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = MathEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = state.botDifficulty.displayName.substringBefore(" "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MathEmerald
                        )
                    }
                    Text(
                        text = "${state.botScore}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "x${state.botMultiplier} (${state.botStreak} seri)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score Ratio Bar
            val totalPoints = (state.score + state.botScore).coerceAtLeast(1)
            val playerRatio = (state.score.toFloat() / totalPoints).coerceIn(0.05f, 0.95f)

            LinearProgressIndicator(
                progress = { playerRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MathIndigo,
                trackColor = MathEmerald
            )

            if (state.botStatusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.botStatusMessage,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    val cardBorderColor by animateColorAsState(
        targetValue = when (state.feedback) {
            AnswerFeedback.CORRECT -> MathEmerald
            AnswerFeedback.WRONG -> MathRose
            AnswerFeedback.NONE -> Color.Transparent
        },
        label = "borderColor"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Formula: 14 × 7 =
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${state.currentQuestion.factorA}  ×  ${state.currentQuestion.factorB}  = ",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Answer placeholder / typed digits
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (state.feedback) {
                                    AnswerFeedback.CORRECT -> MathEmerald.copy(alpha = 0.15f)
                                    AnswerFeedback.WRONG -> MathRose.copy(alpha = 0.15f)
                                    AnswerFeedback.NONE -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (state.userTypedAnswer.isEmpty()) "?" else state.userTypedAnswer,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = when (state.feedback) {
                                AnswerFeedback.CORRECT -> MathEmerald
                                AnswerFeedback.WRONG -> MathRose
                                AnswerFeedback.NONE -> if (state.userTypedAnswer.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MathIndigo
                            }
                        )
                    }
                }

                // Wrong Answer Helper Message
                if (state.lastWrongActualAnswer != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MathRose.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Doğru Cevap: ${state.lastWrongActualAnswer}",
                            color = MathRose,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                } else if (state.feedback == AnswerFeedback.CORRECT) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "+${10 * state.multiplier} Puan!",
                        color = MathEmerald,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
