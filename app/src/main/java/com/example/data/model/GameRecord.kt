package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val stars: Int,
    val timeLimit: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val maxStreak: Int,
    val highestMultiplier: Int,
    val mode: String,
    val botScore: Int = 0,
    val wonAgainstBot: Boolean = false,
    val selectedNumbersSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
