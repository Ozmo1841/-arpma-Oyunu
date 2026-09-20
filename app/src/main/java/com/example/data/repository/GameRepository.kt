package com.example.data.repository

import com.example.data.db.GameDao
import com.example.data.model.GameRecord
import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val allRecords: Flow<List<GameRecord>> = gameDao.getAllRecords()
    val highScore: Flow<Int?> = gameDao.getHighScore()

    suspend fun saveRecord(record: GameRecord) = gameDao.insertRecord(record)
    suspend fun clearHistory() = gameDao.clearAll()
}
