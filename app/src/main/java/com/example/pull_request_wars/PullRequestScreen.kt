package com.example.pull_request_wars

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PullRequestScreen(viewModel: PullRequestViewModel) {
    val gameState by viewModel.uiState.collectAsState()

    // Animac fluida para el icono del repositorio basado en el score
    val offsetX by animateDpAsState(targetValue = (gameState.scoreBalance * 5).dp, label = "repo_movement")

    Box(modifier = Modifier.fillMaxSize()) {
        // ZONAS TACTILES (Pantalla dividida)
        Row(modifier = Modifier.fillMaxSize()) {
            // Zona Equipo 1 (Izquierda)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFE57373)) // Rojo suave
                    .clickable(enabled = gameState.isGameActive) { viewModel.pull(isTeam1 = true) },
                contentAlignment = Alignment.CenterStart
            ) {
                Text("EQUIPO 1\n(Tap!)", modifier = Modifier.padding(16.dp), color = Color.White)
            }

            // Zona Equipo 2 (Derecha)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF64B5F6)) // Azul suave
                    .clickable(enabled = gameState.isGameActive) { viewModel.pull(isTeam1 = false) },
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("EQUIPO 2\n(Tap!)", modifier = Modifier.padding(16.dp), color = Color.White)
            }
        }

        // ELEMENTOS CENTRALES
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Temporizador
            Text(
                text = "00:${gameState.timeLeft.toString().padStart(2, '0')}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // El "Repositorio" que se mueve
            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(80.dp)
                    .background(Color.DarkGray, shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text("REPO", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje de Victoria
            if (!gameState.isGameActive) {
                if (gameState.winner.isNotEmpty()) {
                    Text(text = gameState.winner, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Button(onClick = { viewModel.startGame() }) {
                    Text("Hacer Commit (Start)")
                }
            }
        }
    }
}