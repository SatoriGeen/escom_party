package com.example.pull_request_wars

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pull_request_wars.ui.theme.PULL_REQUEST_WARSTheme

// definimos las pantallas posibles
enum class Screen { MENU, PULL_REQUEST, BINARY_INVADERS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PULL_REQUEST_WARSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EscomPartyApp()
                }
            }
        }
    }
}

@Composable
fun EscomPartyApp() {
    // Estado para saber en qué pantalla estamos
    var currentScreen by remember { mutableStateOf(Screen.MENU) }

    // instanciamos ambos motores de juego aquí para que no se pierdan los datos al cambiar de pantalla
    val prViewModel: PullRequestViewModel = viewModel()
    val invadersViewModel: BinaryInvadersViewModel = viewModel()

    // sistema de navegacion simple
    when (currentScreen) {
        Screen.MENU -> {
            MainMenu(
                onPlayPullRequest = { currentScreen = Screen.PULL_REQUEST },
                onPlayBinaryInvaders = { currentScreen = Screen.BINARY_INVADERS }
            )
        }
        Screen.PULL_REQUEST -> {
            // Un Box para superponer un botón de volver sobre el juego
            Box(modifier = Modifier.fillMaxSize()) {
                PullRequestScreen(viewModel = prViewModel)
                Button(
                    onClick = { currentScreen = Screen.MENU },
                    modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)
                ) {
                    Text("Volver al Menú")
                }
            }
        }
        Screen.BINARY_INVADERS -> {
            Box(modifier = Modifier.fillMaxSize()) {
                BinaryInvadersScreen(viewModel = invadersViewModel)
                Button(
                    onClick = { currentScreen = Screen.MENU },
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter)
                ) {
                    Text("Volver al Menú")
                }
            }
        }
    }
}

@Composable
fun MainMenu(onPlayPullRequest: () -> Unit, onPlayBinaryInvaders: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ESCOM PARTY", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Text("Versión de Prueba", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onPlayPullRequest, modifier = Modifier.fillMaxWidth(0.6f)) {
            Text("Jugar: Pull Request Wars", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onPlayBinaryInvaders, modifier = Modifier.fillMaxWidth(0.6f)) {
            Text("Jugar: Binary Invaders", modifier = Modifier.padding(8.dp))
        }
    }
}