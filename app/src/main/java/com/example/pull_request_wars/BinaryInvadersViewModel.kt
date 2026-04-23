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
import kotlin.random.Random

// Representa a un "enemigo" que cae
data class Bug(
    val id: Int,
    var xPercent: Float, // Posición horizontal (0.0 a 1.0) dentro de su mitad de pantalla
    var yPercent: Float, // Posición vertical (0.0 a 1.0)
    val text: String,
    val isTeam1Side: Boolean
)

data class InvadersGameState(
    val timeLeft: Int = 30, // 30 segundos de juego
    val scoreTeam1: Int = 0,
    val scoreTeam2: Int = 0,
    val activeBugs: List<Bug> = emptyList(),
    val isGameActive: Boolean = false,
    val winner: String = ""
)

class BinaryInvadersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InvadersGameState())
    val uiState: StateFlow<InvadersGameState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var timerJob: Job? = null
    private var bugIdCounter = 0

    private val bugLabels = listOf("NullPtr", "Missing ';'", "IndexOut", "NaN", "TypeErr")

    fun startGame() {
        _uiState.value = InvadersGameState(isGameActive = true)

        // tem principal (segs)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 30 downTo 0) {
                _uiState.update { it.copy(timeLeft = i) }
                delay(1000L)
            }
            endGame()
        }

        // game Loop
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (_uiState.value.isGameActive) {
                updateBugs()
                spawnBugsRandomly()
                delay(50L) // 50ms por frame
            }
        }
    }

    private fun updateBugs() {
        _uiState.update { state ->
            val updatedBugs = state.activeBugs.map { it.copy(yPercent = it.yPercent + 0.015f) } // Velocidad de caída

            // separar los que tocaron fondo de los que siguen cayendo
            val (crashedBugs, survivingBugs) = updatedBugs.partition { it.yPercent >= 1.0f }

            // penalización por dejar caer errores
            var p1Penalty = 0
            var p2Penalty = 0
            crashedBugs.forEach {
                if (it.isTeam1Side) p1Penalty++ else p2Penalty++
            }

            state.copy(
                activeBugs = survivingBugs,
                scoreTeam1 = state.scoreTeam1 - p1Penalty,
                scoreTeam2 = state.scoreTeam2 - p2Penalty
            )
        }
    }

    private fun spawnBugsRandomly() {
        // 10% de probabilidad de generar un bug por cada frame (para cada lado)
        if (Random.nextFloat() < 0.10f) {
            val isP1 = Random.nextBoolean()
            val newBug = Bug(
                id = bugIdCounter++,
                xPercent = Random.nextFloat() * 0.8f, // Evitar que salgan muy en la orilla
                yPercent = 0.0f,
                text = bugLabels.random(),
                isTeam1Side = isP1
            )
            _uiState.update { state ->
                state.copy(activeBugs = state.activeBugs + newBug)
            }
        }
    }

    fun tapBug(bugId: Int, isTeam1: Boolean) {
        if (!_uiState.value.isGameActive) return

        _uiState.update { state ->
            // bbuscar el bug que se tocó
            val bugTapped = state.activeBugs.find { it.id == bugId && it.isTeam1Side == isTeam1 }

            if (bugTapped != null) {
                // eliminar el bug y sumar puntos
                val remainingBugs = state.activeBugs.filterNot { it.id == bugId }
                state.copy(
                    activeBugs = remainingBugs,
                    scoreTeam1 = if (isTeam1) state.scoreTeam1 + 10 else state.scoreTeam1,
                    scoreTeam2 = if (!isTeam1) state.scoreTeam2 + 10 else state.scoreTeam2
                )
            } else {
                state
            }
        }
    }

    private fun endGame() {
        gameLoopJob?.cancel()
        _uiState.update { state ->
            val finalWinner = when {
                state.scoreTeam1 > state.scoreTeam2 -> "¡Equipo 1 Compila Exitosamente!"
                state.scoreTeam2 > state.scoreTeam1 -> "¡Equipo 2 Compila Exitosamente!"
                else -> "Empate: Servidor Caído"
            }
            state.copy(isGameActive = false, activeBugs = emptyList(), winner = finalWinner)
        }
    }
}