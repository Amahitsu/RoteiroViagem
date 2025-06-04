package com.example.roteiroviagem.components

import android.os.Build
import android.util.Log
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
import com.example.roteiroviagem.viewmodel.RoteiroViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun RoadMapSugestionButton(
    trip: Trip,
    roteiroRepository: RoteiroRepository,
    navController: NavController,
    roteiroViewModel: RoteiroViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var suggestion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentRoteiro by remember { mutableStateOf<Roteiro?>(null) }

    var recusaMode by remember { mutableStateOf(false) }
    var motivoRecusa by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun calcularDias(startMillis: Long, endMillis: Long): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val zone = ZoneId.systemDefault()
            val startDate = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()
            val endDate = Instant.ofEpochMilli(endMillis).atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()
            ChronoUnit.DAYS.between(startDate, endDate) + 1
        } else {
            // Para versões mais antigas, faça o cálculo arredondando para cima
            val diffMillis = endMillis - startMillis
            val oneDayMillis = 1000 * 60 * 60 * 24
            (diffMillis + oneDayMillis - 1) / oneDayMillis
        }
    }

    fun loadRoteiro(destino: String, userId: String, orcamento: Double) {
        isLoading = true
        errorMessage = null

        val dias = calcularDias(trip.startDate, trip.endDate)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val startDate =
                Instant.ofEpochMilli(trip.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val endDate =
                Instant.ofEpochMilli(trip.endDate).atZone(ZoneId.systemDefault()).toLocalDate()
            Log.d("DIAS_VIAGEM", "Início: $startDate, Fim: $endDate, Dias: $dias")
        }

        coroutineScope.launch {
            try {
                val roteiro = roteiroRepository.obterRoteiro(destino, userId, dias, orcamento)
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
            recusaMode = false
            motivoRecusa = ""
            loadRoteiro(trip.destination, trip.username, trip.orcamento)
        }) {
            Text("Sugestões para ${trip.destination}")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                recusaMode = false
                motivoRecusa = ""
            },
            title = { Text("Sugestão de Roteiro") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.80f)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
                    when {
                        isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        errorMessage != null -> Text("Erro: $errorMessage")
                        recusaMode -> {
                            Column {
                                Text("Informe o motivo para recusar ou o que deseja mudar:")
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = motivoRecusa,
                                    onValueChange = { motivoRecusa = it },
                                    placeholder = { Text("Motivo ou alteração") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        else -> Text(text = suggestion.ifBlank { "Nenhuma sugestão disponível." })
                    }
                }
            },
            confirmButton = {
                if (recusaMode) {
                    TextButton(
                        onClick = {
                            currentRoteiro?.let {
                                isLoading = true
                                val dias = calcularDias(trip.startDate, trip.endDate)
                                coroutineScope.launch {
                                    try {
                                        roteiroViewModel.recusarERetornarOutro(
                                            it.destino,
                                            dias,
                                            trip.orcamento,
                                            motivoRecusa,
                                        )
                                        currentRoteiro = roteiroViewModel.roteiro.value
                                        suggestion = currentRoteiro?.sugestao ?: ""
                                        recusaMode = false
                                        motivoRecusa = ""
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Erro ao recusar: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        enabled = motivoRecusa.isNotBlank() && !isLoading
                    ) {
                        Text("Enviar")
                    }
                } else {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    currentRoteiro?.let {
                                        val roteiroAceito = it.copy(aceito = true)
                                        if (roteiroAceito.id == 0) {
                                            roteiroViewModel.aceitarRoteiro(roteiroAceito)
                                        } else {
                                            roteiroViewModel.aceitarRoteiro(it)
                                        }
                                        Toast.makeText(
                                            context,
                                            "Roteiro aceito e salvo!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("roteiroSalvo/${it.destino}")
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Erro ao salvar: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } finally {
                                    showDialog = false
                                }
                            }
                        },
                        enabled = !isLoading && currentRoteiro != null
                    ) {
                        Text("Aceitar e Salvar")
                    }
                }
            },
            dismissButton = {
                if (recusaMode) {
                    TextButton(
                        onClick = {
                            recusaMode = false
                            motivoRecusa = ""
                        }
                    ) {
                        Text("Cancelar")
                    }
                } else {
                    TextButton(
                        onClick = {
                            recusaMode = true
                        }
                    ) {
                        Text("Recusar e Solicitar Outro")
                    }
                }
            }
        )
    }
}
