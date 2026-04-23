package com.example.pull_request_wars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BinaryInvadersScreen(viewModel: BinaryInvadersViewModel) {
    val gameState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // HUD SUPERIOR (Marcadores y Tiempo)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("P1 Score: ${gameState.scoreTeam1}", color = Color(0xFFE57373), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("00:${gameState.timeLeft.toString().padStart(2, '0')}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("P2 Score: ${gameState.scoreTeam2}", color = Color(0xFF64B5F6), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        // AREA DE JUEGO
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val zoneWidth = maxWidth / 2
            val zoneHeight = maxHeight

            // Fondo Equipo 1
            Box(
                modifier = Modifier
                    .width(zoneWidth)
                    .fillMaxHeight()
                    .background(Color(0xFF2C2C2C))
                    .align(Alignment.CenterStart)
            )

            // Fondo Equipo 2
            Box(
                modifier = Modifier
                    .width(zoneWidth)
                    .fillMaxHeight()
                    .background(Color(0xFF1E1E1E))
                    .align(Alignment.CenterEnd)
            )

            // DIBUJAR LOS BUGS
            gameState.activeBugs.forEach { bug ->
                val xOffset = if (bug.isTeam1Side) {
                    zoneWidth * bug.xPercent
                } else {
                    zoneWidth + (zoneWidth * bug.xPercent)
                }

                val yOffset = zoneHeight * bug.yPercent

                Box(
                    modifier = Modifier
                        .offset(x = xOffset, y = yOffset)
                        .background(Color(0xFF4CAF50), shape = RoundedCornerShape(8.dp)) // Verde consola
                        .clickable { viewModel.tapBug(bug.id, bug.isTeam1Side) }
                        .padding(8.dp)
                ) {
                    Text(bug.text, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // MENÚ DE FIN DE JUEGO / INICIO
            if (!gameState.isGameActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (gameState.winner.isNotEmpty()) {
                            Text(gameState.winner, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Button(onClick = { viewModel.startGame() }) {
                            Text("Iniciar Compilación (Start)")
                        }
                    }
                }
            }
        }
    }
}