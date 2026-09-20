package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.GameRecord
import com.example.data.repository.GameRepository
import com.example.util.SoundHaptics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameMode(val displayName: String) {
    SOLO("Tek Başına"),
    VS_BOT("Bot ile Kapışma")
}

enum class BotDifficulty(val displayName: String, val speedMs: Long, val accuracyRate: Float) {
    EASY("Kolay (Çırak Bot)", 4200L, 0.65f),
    NORMAL("Normal (Usta Bot)", 2800L, 0.85f),
    HARD("Zor (Soylu Botu 🔥)", 1600L, 0.96f)
}

data class Question(
    val factorA: Int,
    val factorB: Int
) {
    val answer: Int = factorA * factorB
}

enum class AnswerFeedback {
    NONE,
    CORRECT,
    WRONG
}

data class GameUiState(
    // Setup Settings
    val selectedDurationSeconds: Int = 60,
    val selectedNumbers: Set<Int> = (1..10).toSet(),
    val gameMode: GameMode = GameMode.SOLO,
    val botDifficulty: BotDifficulty = BotDifficulty.NORMAL,
    val autoSubmitOnMatch: Boolean = true,

    // Active Game State
    val isGameActive: Boolean = false,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val remainingSeconds: Int = 60,

    // Current Question & Input
    val currentQuestion: Question = Question(7, 8),
    val userTypedAnswer: String = "",
    val feedback: AnswerFeedback = AnswerFeedback.NONE,
    val lastWrongActualAnswer: Int? = null,

    // Player Progress
    val score: Int = 0,
    val currentStreak: Int = 0,
    val multiplier: Int = 1,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val maxStreak: Int = 0,
    val highestMultiplier: Int = 1,

    // Bot Progress (for VS_BOT mode)
    val botScore: Int = 0,
    val botStreak: Int = 0,
    val botMultiplier: Int = 1,
    val botCorrectCount: Int = 0,
    val botWrongCount: Int = 0,
    val botStatusMessage: String = "",

    // Milestone Toast / Banner
    val milestoneEvent: String? = null
) {
    val progressToNextMultiplier: Float
        get() {
            if (multiplier >= 5) return 1f
            val withinCurrentTier = currentStreak % 10
            return withinCurrentTier / 10f
        }

    val streakNeededForNextMultiplier: Int
        get() {
            if (multiplier >= 5) return 0
            val target = multiplier * 10
            return target - currentStreak
        }

    val currentStars: Int
        get() = calculateStars(score)

    companion object {
        fun calculateStars(score: Int): Int {
            return when {
                score >= 1500 -> 3
                score >= 640 -> 2
                else -> 1
            }
        }
    }
}

class MultiplicationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository
    val soundHaptics = SoundHaptics(application)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameDao())
    }

    val gameHistory: StateFlow<List<GameRecord>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTimeHighScore: StateFlow<Int?> = repository.highScore
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var botJob: Job? = null
    private var feedbackJob: Job? = null

    // Allowed durations: 60, 70, 80, ... 200
    val availableDurations = (60..200 step 10).toList()

    fun setDuration(seconds: Int) {
        if (!_uiState.value.isGameActive) {
            _uiState.value = _uiState.value.copy(
                selectedDurationSeconds = seconds,
                remainingSeconds = seconds
            )
        }
    }

    fun toggleNumber(num: Int) {
        if (_uiState.value.isGameActive) return
        val current = _uiState.value.selectedNumbers.toMutableSet()
        if (current.contains(num)) {
            if (current.size > 1) {
                current.remove(num)
            }
        } else {
            current.add(num)
        }
        _uiState.value = _uiState.value.copy(selectedNumbers = current)
    }

    fun selectNumberPreset(preset: String) {
        if (_uiState.value.isGameActive) return
        val newSet = when (preset) {
            "0-10" -> (0..10).toSet()
            "1-10" -> (1..10).toSet()
            "0-20" -> (0..20).toSet()
            "1-20" -> (1..20).toSet()
            "Zorlar" -> setOf(6, 7, 8, 9, 12, 13, 14, 15, 16, 17, 18, 19, 20)
            "Kolay" -> setOf(1, 2, 3, 4, 5, 10)
            else -> (1..10).toSet()
        }
        _uiState.value = _uiState.value.copy(selectedNumbers = newSet)
    }

    fun setGameMode(mode: GameMode) {
        if (!_uiState.value.isGameActive) {
            _uiState.value = _uiState.value.copy(gameMode = mode)
        }
    }

    fun setBotDifficulty(difficulty: BotDifficulty) {
        if (!_uiState.value.isGameActive) {
            _uiState.value = _uiState.value.copy(botDifficulty = difficulty)
        }
    }

    fun toggleAutoSubmit() {
        _uiState.value = _uiState.value.copy(autoSubmitOnMatch = !_uiState.value.autoSubmitOnMatch)
    }

    fun startGame() {
        val initialQuestion = generateQuestion(_uiState.value.selectedNumbers)
        val duration = _uiState.value.selectedDurationSeconds

        _uiState.value = _uiState.value.copy(
            isGameActive = true,
            isPaused = false,
            isGameOver = false,
            remainingSeconds = duration,
            currentQuestion = initialQuestion,
            userTypedAnswer = "",
            feedback = AnswerFeedback.NONE,
            lastWrongActualAnswer = null,
            score = 0,
            currentStreak = 0,
            multiplier = 1,
            correctCount = 0,
            wrongCount = 0,
            maxStreak = 0,
            highestMultiplier = 1,
            botScore = 0,
            botStreak = 0,
            botMultiplier = 1,
            botCorrectCount = 0,
            botWrongCount = 0,
            botStatusMessage = if (_uiState.value.gameMode == GameMode.VS_BOT) "Bot hazır!" else "",
            milestoneEvent = null
        )

        startTimer()

        if (_uiState.value.gameMode == GameMode.VS_BOT) {
            startBotWorker()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.remainingSeconds > 0) {
                delay(1000L)
                if (!_uiState.value.isPaused && _uiState.value.isGameActive) {
                    val newSec = _uiState.value.remainingSeconds - 1
                    _uiState.value = _uiState.value.copy(remainingSeconds = newSec)
                    if (newSec <= 0) {
                        endGame()
                        break
                    }
                }
            }
        }
    }

    private fun startBotWorker() {
        botJob?.cancel()
        botJob = viewModelScope.launch {
            val bot = _uiState.value.botDifficulty
            while (isActive && _uiState.value.isGameActive) {
                // Bot thinks for interval +/- variance
                val variance = Random.nextLong(-300, 400)
                val thinkTime = (bot.speedMs + variance).coerceAtLeast(800L)
                delay(thinkTime)

                if (!_uiState.value.isPaused && _uiState.value.isGameActive) {
                    val isCorrect = Random.nextFloat() <= bot.accuracyRate
                    val currentState = _uiState.value

                    if (isCorrect) {
                        val newStreak = currentState.botStreak + 1
                        val newMultiplier = calculateMultiplier(newStreak)
                        val pointsEarned = 10 * newMultiplier
                        val newScore = currentState.botScore + pointsEarned

                        _uiState.value = currentState.copy(
                            botScore = newScore,
                            botStreak = newStreak,
                            botMultiplier = newMultiplier,
                            botCorrectCount = currentState.botCorrectCount + 1,
                            botStatusMessage = "Bot doğru bildi! (+$pointsEarned)"
                        )
                    } else {
                        _uiState.value = currentState.copy(
                            botStreak = 0,
                            botMultiplier = 1,
                            botWrongCount = currentState.botWrongCount + 1,
                            botStatusMessage = "Bot hata yaptı!"
                        )
                    }
                }
            }
        }
    }

    fun pauseResumeGame() {
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
    }

    fun stopGameEarly() {
        endGame()
    }

    fun appendDigit(digit: Int) {
        val currentState = _uiState.value
        if (!currentState.isGameActive || currentState.isPaused || currentState.isGameOver) return
        if (currentState.userTypedAnswer.length >= 4) return // Max 4 digits (e.g. 20x20 = 400)

        val newAnswer = currentState.userTypedAnswer + digit.toString()
        _uiState.value = currentState.copy(userTypedAnswer = newAnswer)

        if (currentState.autoSubmitOnMatch) {
            val typedNum = newAnswer.toIntOrNull()
            if (typedNum != null && typedNum == currentState.currentQuestion.answer) {
                submitAnswer()
            }
        }
    }

    fun deleteLastDigit() {
        val currentState = _uiState.value
        if (!currentState.isGameActive || currentState.isPaused || currentState.isGameOver) return
        if (currentState.userTypedAnswer.isNotEmpty()) {
            _uiState.value = currentState.copy(
                userTypedAnswer = currentState.userTypedAnswer.dropLast(1)
            )
        }
    }

    fun clearInput() {
        val currentState = _uiState.value
        if (!currentState.isGameActive || currentState.isPaused || currentState.isGameOver) return
        _uiState.value = currentState.copy(userTypedAnswer = "")
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (!currentState.isGameActive || currentState.isPaused || currentState.isGameOver) return
        val typedNum = currentState.userTypedAnswer.toIntOrNull() ?: return

        val isCorrect = typedNum == currentState.currentQuestion.answer
        feedbackJob?.cancel()

        if (isCorrect) {
            soundHaptics.playCorrect()
            val oldStreak = currentState.currentStreak
            val newStreak = oldStreak + 1
            val newMultiplier = calculateMultiplier(newStreak)

            if (newMultiplier > currentState.multiplier) {
                soundHaptics.playMultiplierUp()
            }

            val earnedPoints = 10 * newMultiplier
            val newScore = currentState.score + earnedPoints
            val newMaxStreak = maxOf(currentState.maxStreak, newStreak)
            val newHighestMult = maxOf(currentState.highestMultiplier, newMultiplier)

            // Check star milestone announcements
            var milestone: String? = null
            if (currentState.score < 640 && newScore >= 640) {
                milestone = "⭐⭐ 2. Yıldız Açıldı! (640+ Puan)"
            } else if (currentState.score < 1500 && newScore >= 1500) {
                milestone = "🌟🌟🌟 3. Yıldız Açıldı! (1500+ Puan)"
            } else if (newMultiplier > currentState.multiplier) {
                milestone = "🔥 x$newMultiplier Çarpan Aktif!"
            }

            _uiState.value = currentState.copy(
                score = newScore,
                currentStreak = newStreak,
                multiplier = newMultiplier,
                correctCount = currentState.correctCount + 1,
                maxStreak = newMaxStreak,
                highestMultiplier = newHighestMult,
                userTypedAnswer = "",
                feedback = AnswerFeedback.CORRECT,
                lastWrongActualAnswer = null,
                currentQuestion = generateQuestion(currentState.selectedNumbers, currentState.currentQuestion),
                milestoneEvent = milestone
            )

            feedbackJob = viewModelScope.launch {
                delay(300L)
                _uiState.value = _uiState.value.copy(feedback = AnswerFeedback.NONE)
                if (milestone != null) {
                    delay(1500L)
                    _uiState.value = _uiState.value.copy(milestoneEvent = null)
                }
            }
        } else {
            soundHaptics.playWrong()
            val wrongAnswer = currentState.currentQuestion.answer

            _uiState.value = currentState.copy(
                currentStreak = 0,
                multiplier = 1,
                wrongCount = currentState.wrongCount + 1,
                userTypedAnswer = "",
                feedback = AnswerFeedback.WRONG,
                lastWrongActualAnswer = wrongAnswer,
                milestoneEvent = "Hata! Seri sıfırlandı."
            )

            feedbackJob = viewModelScope.launch {
                delay(700L)
                _uiState.value = _uiState.value.copy(
                    feedback = AnswerFeedback.NONE,
                    lastWrongActualAnswer = null,
                    milestoneEvent = null,
                    currentQuestion = generateQuestion(currentState.selectedNumbers, currentState.currentQuestion)
                )
            }
        }
    }

    private fun calculateMultiplier(streak: Int): Int {
        return when {
            streak >= 40 -> 5
            streak >= 30 -> 4
            streak >= 20 -> 3
            streak >= 10 -> 2
            else -> 1
        }
    }

    private fun generateQuestion(selectedNumbers: Set<Int>, previous: Question? = null): Question {
        val pool = if (selectedNumbers.isEmpty()) (1..10).toList() else selectedNumbers.toList()

        var attempts = 0
        while (attempts < 20) {
            attempts++
            // Choose factorA from selected numbers
            val a = pool.random()
            // FactorB: can be from pool, or from 0..20
            val b = if (pool.size > 8 && Random.nextBoolean()) {
                pool.random()
            } else {
                Random.nextInt(0, 21)
            }

            // Mix order randomly (A x B or B x A)
            val (factor1, factor2) = if (Random.nextBoolean()) Pair(a, b) else Pair(b, a)
            val candidate = Question(factor1, factor2)

            if (previous == null || candidate != previous) {
                return candidate
            }
        }
        return Question(pool.random(), Random.nextInt(0, 21))
    }

    private fun endGame() {
        timerJob?.cancel()
        botJob?.cancel()
        soundHaptics.playFinish()

        val state = _uiState.value
        val stars = GameUiState.calculateStars(state.score)
        val isVsBot = state.gameMode == GameMode.VS_BOT
        val wonBot = isVsBot && state.score > state.botScore

        val modeLabel = if (isVsBot) "Bot (${state.botDifficulty.displayName})" else "Tek Başına"
        val numbersSummary = if (state.selectedNumbers.size == 21) {
            "0-20 (Tümü)"
        } else if (state.selectedNumbers == (1..10).toSet()) {
            "1-10"
        } else {
            state.selectedNumbers.sorted().joinToString(",")
        }

        val record = GameRecord(
            score = state.score,
            stars = stars,
            timeLimit = state.selectedDurationSeconds,
            correctCount = state.correctCount,
            wrongCount = state.wrongCount,
            maxStreak = state.maxStreak,
            highestMultiplier = state.highestMultiplier,
            mode = modeLabel,
            botScore = if (isVsBot) state.botScore else 0,
            wonAgainstBot = wonBot,
            selectedNumbersSummary = numbersSummary
        )

        viewModelScope.launch {
            repository.saveRecord(record)
        }

        _uiState.value = state.copy(
            isGameActive = false,
            isGameOver = true
        )
    }

    fun dismissGameOver() {
        _uiState.value = _uiState.value.copy(
            isGameOver = false,
            remainingSeconds = _uiState.value.selectedDurationSeconds,
            userTypedAnswer = "",
            feedback = AnswerFeedback.NONE
        )
    }

    fun clearScores() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundHaptics.release()
    }
}
