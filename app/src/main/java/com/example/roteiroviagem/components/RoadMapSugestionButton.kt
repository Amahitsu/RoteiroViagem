package com.example.roteiroviagem.components

import android.R.attr.maxLines
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.entity.Roteiro
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.viewmodel.RoteiroViewModel
import com.example.roteiroviagem.viewmodels.RoteiroViewModelFactory
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit


fun calcularDiasViagem(startDate: Long, endDate: Long): Int {
    val diffMillis = endDate - startDate
    val dias = (diffMillis / (1000 * 60 * 60 * 24)).toInt() + 1
    return dias.coerceAtLeast(1)
}

@Composable
fun RoadMapSugestionButton(trip: Trip) {
    val context = LocalContext.current

    var extraRequest by remember { mutableStateOf("") }  // Novo campo para pedido extra
    val roteiroDao = AppDatabase.getDatabase(context).roteiroDao()
    val roteiroRepository = RoteiroRepository(roteiroDao)
    val roteiroViewModel: RoteiroViewModel = viewModel(factory = RoteiroViewModelFactory(roteiroRepository))

    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var suggestion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val orcamentoFormatado = "%.2f".format(trip.orcamento).replace('.', ',')

    val days = calcularDiasViagem(trip.startDate, trip.endDate)

    Column(
        modifier = Modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            isLoading = true
            showDialog = true
            errorMessage = null
            suggestion = ""
            extraRequest = ""

            val prompt = "Sugira um roteiro de ${days} ${if (days == 1) "dia" else "dias"} para ${trip.destination} para uma viagem do tipo ${trip.type}, incluindo dicas locais, culinária e pontos turísticos." +
                    " Dentro do orçamento total disponível sendo de R$ $orcamentoFormatado. Este valor está em reais, não em milhares."

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
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (errorMessage != null) {
                    Text("Erro: $errorMessage")
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Text(text = suggestion)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Novo campo TextField para pedido extra
                        OutlinedTextField(
                            value = extraRequest,
                            onValueChange = { extraRequest = it },
                            label = { Text("Nova sugestão...") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {
                            Button(onClick = {
                                roteiroViewModel.salvarRoteiro(
                                    username = trip.username,
                                    destino = trip.destination,
                                    tripId = trip.id,
                                    sugestao = suggestion
                                )
                                Toast.makeText(context, "Roteiro salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                showDialog = false
                            },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp) // altura consistente
                            ) {
                                Text("Salvar")
                            }

                            Button(onClick = {
                                if (extraRequest.isBlank()) {
                                    Toast.makeText(context, "Digite um pedido extra para a sugestão", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isLoading = true
                                suggestion = ""
                                errorMessage = null

                                val prompt = "Sugira um roteiro de ${days} ${if (days == 1) "dia" else "dias"} para ${trip.destination} para uma viagem do tipo ${trip.type}, incluindo dicas locais, culinária e pontos turísticos. " +
                                        "O orçamento total disponível é de R$ $orcamentoFormatado. Este valor está em reais, não em milhares. Além disso, inclua o seguinte pedido: ${extraRequest}."

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
                            },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp), // altura consistente
                            ){
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Gerar nova sugestão")
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}