package com.example.roteiroviagem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.screens.calcularDiasViagem


@Composable
fun RoadMapSugestionButton(trip: Trip) {
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var suggestion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val days = calcularDiasViagem(trip.startDate, trip.endDate)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            isLoading = true
            showDialog = true
            errorMessage = null
            suggestion = ""

            val prompt = "Sugira um roteiro de ${days} ${if (days == 1) "dia" else "dias"} para ${trip.destination}, incluindo dicas locais, culinária e pontos turísticos."

            GeminiService.sugerirRoteiro(
                destino = prompt,
                onResult = {
                    suggestion = it
                    isLoading = false
                },
                onError = {
                    errorMessage = it
                    isLoading = false
                }
            )
        }) {
            Text("Sugestões para ${trip.destination}")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Fechar")
                }
            },
            title = { Text("Sugestão de Roteiro") },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else if (errorMessage != null) {
                        Text("Erro: $errorMessage")
                    } else {
                        Text(suggestion)
                    }
                }
            }
        )
    }
}

