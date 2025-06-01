package com.example.roteiroviagem.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.entity.Roteiro
import com.example.roteiroviagem.entity.Trip
import kotlinx.coroutines.launch

@Composable
fun RoadMapSugestionButton(
    trip: Trip,
    roteiroRepository: RoteiroRepository,
    navController: NavController
) {
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var suggestion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentRoteiro by remember { mutableStateOf<Roteiro?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun loadRoteiro(destino: String) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                val roteiro = roteiroRepository.obterRoteiro(destino)
                suggestion = roteiro.sugestao
                currentRoteiro = roteiro
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro desconhecido"
                suggestion = ""
                currentRoteiro = null
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            showDialog = true
            loadRoteiro(trip.destination)
        }) {
            Text("Sugestões para ${trip.destination}")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Sugestão de Roteiro") },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
                    when {
                        isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        errorMessage != null -> Text("Erro: $errorMessage")
                        suggestion.isNotBlank() -> Text(suggestion)
                        else -> Text("Nenhuma sugestão disponível.")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                currentRoteiro?.let {
                                    roteiroRepository.aceitarRoteiro(it)
                                    Toast.makeText(context, "Roteiro aceito e salvo!", Toast.LENGTH_SHORT).show()

                                    // Navegar para nova tela após salvar
                                    navController.navigate("roteiroSalvo/${it.destino}")
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                showDialog = false
                            }
                        }
                    },
                    enabled = !isLoading && currentRoteiro != null
                ) {
                    Text("Aceitar e Salvar")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        if (currentRoteiro != null) {
                            loadRoteiro(currentRoteiro!!.destino)
                        }
                    },
                    enabled = !isLoading && currentRoteiro != null
                ) {
                    Text("Recusar e Sugerir Outro")
                }
            }
        )
    }
}
