package com.example.roteiroviagem.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.rememberDismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roteiroviagem.R
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme
import com.example.roteiroviagem.viewmodels.TripViewModel
import com.example.roteiroviagem.viewmodels.TripViewModelFactory
import com.example.roteiroviagem.components.RoadMapSugestionButton

// Util
import java.text.SimpleDateFormat
import java.util.*

fun calcularDiasViagem(startDate: Long, endDate: Long): Int {
    val diffMillis = endDate - startDate
    val dias = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    return dias.coerceAtLeast(1)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val tripViewModel: TripViewModel = viewModel(factory = TripViewModelFactory(tripDao, username))
    val tripList by tripViewModel.trips.collectAsState()

    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (tripList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma viagem cadastrada.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tripList) { trip ->
                        TripItem(
                            trip = trip,
                            onDelete = { tripViewModel.deleteTrip(trip) },
                            onEdit = { navController.navigate("edit_trip/${trip.id}/$username") }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TripItem(
    trip: Trip,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            when (it) {
                DismissValue.DismissedToStart -> {
                    onDelete()
                    true
                }
                DismissValue.DismissedToEnd -> {
                    onEdit()
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(
            DismissDirection.StartToEnd,
            DismissDirection.EndToStart
        ),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = when (direction) {
                DismissDirection.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                DismissDirection.EndToStart -> MaterialTheme.colorScheme.errorContainer
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Edit
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == DismissDirection.StartToEnd)
                    Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(icon, contentDescription = null)
            }
        },
        dismissContent = {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val iconRes = if (trip.type == "Business") R.drawable.ic_business else R.drawable.ic_leisure

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onEdit() } // Detecção de clique longo
                        )
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Informações de viagem
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Destino: ${trip.destination}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text("Ida: ${sdf.format(Date(trip.startDate))}")
                            Text("Volta: ${sdf.format(Date(trip.endDate))}")
                            Text("Orçamento: R$ %.2f".format(trip.budget))
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))  // Espaço entre as informações da viagem e o botão

                    // Colocando o botão de sugestão abaixo dos dados
                    RoadMapSugestionButton(trip)
                }
            }
        }
    )
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewMainScreen() {
    RoteiroViagemTheme {
        // Chamando a tela principal diretamente no Preview com um username fixo
        MainScreen(navController = rememberNavController(), username = "testeUsuario")
    }
}