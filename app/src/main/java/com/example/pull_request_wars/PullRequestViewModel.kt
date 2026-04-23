package com.example.pull_request_wars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PRGameState(
    val timeLeft: Int = 10,
    val scoreBalance: Int = 0, // Negativo gana Equipo 1 (Izq) Y Positivo gana Equipo 2 (Der)
    val isGameActive: Boolean = false,
    val winner: String = ""
)

class PullRequestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PRGameState())
    val uiState: StateFlow<PRGameState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startGame() {
        // Reiniciamos el estado
        _uiState.value = PRGameState(isGameActive = true)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 10 downTo 0) {
                _uiState.update { it.copy(timeLeft = i) }
                delay(1000L) // Esperamos 1 segundo
            }
            endGame()
        }
    }

    fun pull(isTeam1: Boolean) {
        if (!_uiState.value.isGameActive) return

        _uiState.update { currentState ->
            // Equipo 1 resta (jala a la izquierda) Y Equipo 2 suma (jala a la derecha)
            val newScore = if (isTeam1) currentState.scoreBalance - 1 else currentState.scoreBalance + 1
            currentState.copy(scoreBalance = newScore)
        }
    }

    private fun endGame() {
        _uiState.update { currentState ->
            val finalWinner = when {
                currentState.scoreBalance < 0 -> "¡Equipo 1 Gana la Estrella!"
                currentState.scoreBalance > 0 -> "¡Equipo 2 Gana la Estrella!"
                else -> "¡Empate! Git Conflict."
            }
            currentState.copy(isGameActive = false, winner = finalWinner)
        }
    }
}