package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.GameMode
import com.example.ui.GameUiState
import com.example.ui.theme.MathAmber
import com.example.ui.theme.MathEmerald
import com.example.ui.theme.MathIndigo
import com.example.ui.theme.MathRose

@Composable
fun GameOverDialog(
    state: GameUiState,
    onRestart: () -> Unit,
    onDismissToSettings: () -> Unit
) {
    val isVsBot = state.gameMode == GameMode.VS_BOT
    val playerWon = isVsBot && state.score > state.botScore
    val isTie = isVsBot && state.score == state.botScore
    val stars = state.currentStars

    val starTitle = when (stars) {
        3 -> "Efsane Başarı! ⭐⭐⭐"
        2 -> "Harika Performans! ⭐⭐"
        else -> "Tebrikler! ⭐"
    }

    val starSubtitle = when (stars) {
        3 -> "1500+ Puan ile 3 Yıldız Kazandın!"
        2 -> "640 - 1500 Puan ile 2 Yıldız Kazandın!"
        else -> "0 - 640 Puan ile 1 Yıldız Kazandın."
    }

    Dialog(
        onDismissRequest = onDismissToSettings,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MathAmber, MathIndigo)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isVsBot) Icons.Filled.EmojiEvents else Icons.Filled.Celebration,
                        contentDescription = "Sonuç",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VS Bot Result Banner
                if (isVsBot) {
                    val resultText = when {
                        playerWon -> "BOTA KARŞI KAZANDIN! 🏆"
                        isTie -> "BERABERE BİTTİ! 🤝"
                        else -> "BOT KAZANDI! 🤖"
                    }
                    val resultColor = if (playerWon) MathEmerald else if (isTie) MathAmber else MathRose

                    Text(
                        text = resultText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = resultColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sen: ${state.score} Puan  vs  Bot: ${state.botScore} Puan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Star Rating
                StarRatingRow(
                    stars = stars,
                    maxStars = 3,
                    starSize = 44,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Text(
                    text = starTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = starSubtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                // Stats Grid Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatRow(
                            label = "Toplam Puan",
                            value = "${state.score}",
                            valueColor = MathIndigo
                        )

                        val totalAnswered = state.correctCount + state.wrongCount
                        val accuracy = if (totalAnswered > 0) {
                            ((state.correctCount.toFloat() / totalAnswered) * 100).toInt()
                        } else 0

                        StatRow(
                            label = "Doğru / Yanlış",
                            value = "${state.correctCount} / ${state.wrongCount}",
                            valueColor = MathEmerald
                        )

                        StatRow(
                            label = "İsabet Oranı",
                            value = "%$accuracy",
                            valueColor = MaterialTheme.colorScheme.onSurface
                        )

                        StatRow(
                            label = "En Yüksek Seri",
                            value = "${state.maxStreak} Soru",
                            valueColor = MathAmber
                        )

                        StatRow(
                            label = "Ulaşılan Çarpan",
                            value = "x${state.highestMultiplier}",
                            valueColor = MathAmber
                        )

                        val qpm = if (state.selectedDurationSeconds > 0) {
                            (totalAnswered * 60f / state.selectedDurationSeconds).toInt()
                        } else 0

                        StatRow(
                            label = "Çarpma Hızı",
                            value = "$qpm soru / dk",
                            valueColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissToSettings,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("dialog_btn_settings"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Ayarlar", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ayarlar", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onRestart,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp)
                            .testTag("dialog_btn_restart"),
                        colors = ButtonDefaults.buttonColors(containerColor = MathIndigo),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Tekrar Oyna", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tekrar Başla", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
