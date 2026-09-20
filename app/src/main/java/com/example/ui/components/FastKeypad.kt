package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.MathAmber
import com.example.ui.theme.MathEmerald
import com.example.ui.theme.MathIndigo
import com.example.ui.theme.MathRose

@Composable
fun StarRatingRow(
    stars: Int,
    maxStars: Int = 3,
    starSize: Int = 32,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isEarned = i <= stars
            val tint = if (isEarned) MathAmber else Color.Gray.copy(alpha = 0.4f)
            Icon(
                imageVector = if (isEarned) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = if (isEarned) "Yıldız $i" else "Kilitli Yıldız",
                tint = tint,
                modifier = Modifier
                    .size(starSize.dp)
                    .padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
fun FastKeypad(
    onDigitClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "DEL")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (item in row) {
                    when (item) {
                        "C" -> {
                            KeypadButton(
                                text = "C",
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                textColor = MathRose,
                                onClick = onClearClick,
                                testTag = "keypad_btn_clear",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        "DEL" -> {
                            KeypadIconButton(
                                icon = Icons.Filled.Backspace,
                                contentDesc = "Sil",
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tint = MaterialTheme.colorScheme.onSurface,
                                onClick = onDeleteClick,
                                testTag = "keypad_btn_delete",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        else -> {
                            val digit = item.toInt()
                            KeypadButton(
                                text = item,
                                color = MaterialTheme.colorScheme.surface,
                                textColor = MaterialTheme.colorScheme.onSurface,
                                onClick = { onDigitClick(digit) },
                                testTag = "keypad_btn_$digit",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Prominent Submit Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onSubmitClick
                )
                .testTag("keypad_btn_submit"),
            color = MathEmerald,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Tamam",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GÖNDER / CEVAPLA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    color: Color,
    textColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun KeypadIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    color: Color,
    tint: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
