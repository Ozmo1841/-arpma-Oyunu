package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.ui.BotDifficulty
import com.example.ui.GameMode
import com.example.ui.GameUiState
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.MathAmber
import com.example.ui.theme.MathCyan
import com.example.ui.theme.MathEmerald
import com.example.ui.theme.MathIndigo
import com.example.ui.theme.MathRose

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    state: GameUiState,
    highScore: Int,
    availableDurations: List<Int>,
    onSelectDuration: (Int) -> Unit,
    onToggleNumber: (Int) -> Unit,
    onSelectPreset: (String) -> Unit,
    onSelectGameMode: (GameMode) -> Unit,
    onSelectBotDifficulty: (BotDifficulty) -> Unit,
    onToggleAutoSubmit: () -> Unit,
    onStartGame: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sebahattin Soylu",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MathIndigo
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MathAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "HIZLI ÇARPMA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MathAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Çarpma Alıştırması",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row {
                    IconButton(
                        onClick = onOpenRules,
                        modifier = Modifier.testTag("btn_open_rules")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HelpOutline,
                            contentDescription = "Kurallar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.testTag("btn_open_history")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Geçmiş",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // High Score & Star Tiers Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = MathAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "En Yüksek Skor:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "$highScore Puan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MathIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Star criteria explanation bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⭐ 0-640", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "•", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "⭐⭐ 640-1500", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "•", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "⭐⭐⭐ 1500+", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MathAmber)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Game Mode Selection (Solo vs Bot)
            Text(
                text = "Oyun Modu",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 22.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ModeCard(
                    title = "Tek Başına",
                    subtitle = "Bireysel hız & kombo rekoru",
                    icon = Icons.Filled.Speed,
                    isSelected = state.gameMode == GameMode.SOLO,
                    onClick = { onSelectGameMode(GameMode.SOLO) },
                    modifier = Modifier.weight(1f),
                    testTag = "mode_solo"
                )

                ModeCard(
                    title = "Bot ile Kapışma",
                    subtitle = "Yapay zeka rakibe karşı duel",
                    icon = Icons.Filled.SmartToy,
                    isSelected = state.gameMode == GameMode.VS_BOT,
                    onClick = { onSelectGameMode(GameMode.VS_BOT) },
                    modifier = Modifier.weight(1f),
                    testTag = "mode_bot"
                )
            }

            // Bot Difficulty (visible only when VS_BOT mode is selected)
            if (state.gameMode == GameMode.VS_BOT) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Bot Zorluk Seviyesi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotDifficulty.values().forEach { diff ->
                        val isSelected = state.botDifficulty == diff
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectBotDifficulty(diff) }
                                .then(
                                    if (isSelected) Modifier.border(2.dp, MathEmerald, RoundedCornerShape(12.dp))
                                    else Modifier
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MathEmerald.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = when (diff) {
                                        BotDifficulty.EASY -> "Kolay"
                                        BotDifficulty.NORMAL -> "Normal"
                                        BotDifficulty.HARD -> "Zor 🔥"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MathEmerald else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (diff) {
                                        BotDifficulty.EASY -> "Çırak"
                                        BotDifficulty.NORMAL -> "Usta"
                                        BotDifficulty.HARD -> "Soylu"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Duration Selector (Süre Seçimi 60 - 200 onar onar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = MathIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Süre Seçimi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MathIndigo.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${state.selectedDurationSeconds} Saniye",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = MathIndigo,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Duration Slider (60 to 200, step 10)
            Slider(
                value = state.selectedDurationSeconds.toFloat(),
                onValueChange = { onSelectDuration(it.toInt()) },
                valueRange = 60f..200f,
                steps = 13, // (200 - 60) / 10 - 1 = 13 steps
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MathIndigo,
                    activeTrackColor = MathIndigo,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            // Quick Duration Chips (60, 90, 100, 120, 150, 200)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(60, 90, 100, 120, 150, 200).forEach { dur ->
                    val isSelected = state.selectedDurationSeconds == dur
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectDuration(dur) }
                            .then(
                                if (isSelected) Modifier.border(1.5.dp, MathIndigo, RoundedCornerShape(8.dp))
                                else Modifier
                            ),
                        color = if (isSelected) MathIndigo.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${dur}s",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MathIndigo else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Multiplication Numbers Selection (0 to 20)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = MathAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Çarpma Sayıları (0 - 20)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${state.selectedNumbers.size} sayı seçildi",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Preset Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("1-10", "1-20", "0-20", "Zorlar", "Kolay").forEach { preset ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectPreset(preset) },
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = preset,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Numbers Grid (0..20)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (num in 0..20) {
                    val isSelected = state.selectedNumbers.contains(num)
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleNumber(num) }
                            .then(
                                if (isSelected) Modifier.border(1.5.dp, MathIndigo, RoundedCornerShape(10.dp))
                                else Modifier
                            ),
                        color = if (isSelected) MathIndigo else MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isSelected) 3.dp else 1.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$num",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Auto submit switch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hızlı Giriş (Otomatik Geçiş)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Doğru sonuç yazıldığı an onay beklemeden yeni soruya geçer.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = state.autoSubmitOnMatch,
                        onCheckedChange = { onToggleAutoSubmit() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MathIndigo,
                            checkedTrackColor = MathIndigo.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Floating Bottom Start Button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.95f), MaterialTheme.colorScheme.background)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            color = Color.Transparent
        ) {
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("btn_start_game"),
                colors = ButtonDefaults.buttonColors(containerColor = MathIndigo),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ÇALIŞMAYI BAŞLAT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .testTag(testTag)
            .then(
                if (isSelected) Modifier.border(2.dp, MathIndigo, RoundedCornerShape(16.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MathIndigo.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MathIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MathIndigo else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
