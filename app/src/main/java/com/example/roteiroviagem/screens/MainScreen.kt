package com.example.roteiroviagem.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roteiroviagem.R
import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.components.RoadMapSugestionButton
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.viewmodels.RoteiroViewModelFactory
import com.example.roteiroviagem.viewmodel.RoteiroViewModel
import com.example.roteiroviagem.viewmodels.TripViewModel
import com.example.roteiroviagem.viewmodels.TripViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val tripDao = database.tripDao()
    val roteiroDao = database.roteiroDao()

    val tripViewModel: TripViewModel = viewModel(factory = TripViewModelFactory(tripDao, username))
    val tripList by tripViewModel.trips.collectAsState()

    val roteiroRepository = remember { RoteiroRepository(roteiroDao, GeminiService) }
    val roteiroViewModel: RoteiroViewModel = viewModel(
        factory = RoteiroViewModelFactory(
            repository = roteiroRepository,
            geminiService = GeminiService,
            userId = username
        )
    )
    Scaffold { innerPadding ->
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
                            roteiroViewModel = roteiroViewModel,
                            roteiroRepository = roteiroRepository,
                            onDelete = { tripViewModel.deleteTrip(trip) },
                            onEdit = { navController.navigate("edit_trip/${trip.id}/$username") },
                            navController = navController // ✅ Corrigido
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TripItem(
    trip: Trip,
    roteiroViewModel: RoteiroViewModel,
    roteiroRepository: RoteiroRepository,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    navController: NavController
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

    // Remover o LaunchedEffect que carregava o roteiro
    val roteiro = roteiroViewModel.roteiro.value

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
                        detectTapGestures(onLongPress = { onEdit() })
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Destino: ${trip.destination}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text("Ida: ${sdf.format(Date(trip.startDate))}")
                            Text("Volta: ${sdf.format(Date(trip.endDate))}")
                            Text("Orçamento: R$ %.2f".format(trip.orcamento))
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

                    Spacer(modifier = Modifier.height(8.dp))

                    roteiro?.let {
                        Text("Roteiro salvo:")
                        Text(it.sugestao)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    if (roteiro == null || roteiro.aceito.not()) {
                        RoadMapSugestionButton(
                            trip = trip,
                            roteiroRepository = roteiroRepository,
                            navController = navController,
                            roteiroViewModel = roteiroViewModel,
                        )
                    } else {
                        Text(
                            "Roteiro aceito",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}