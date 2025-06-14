package com.example.roteiroviagem.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.viewmodels.TripViewModel
import com.example.roteiroviagem.viewmodels.TripViewModelFactory
import com.example.roteiroviagem.R
import com.example.roteiroviagem.components.RoadMapSugestionButton
import com.example.roteiroviagem.dao.RoteiroDao
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, username: String) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val tripViewModel: TripViewModel = viewModel(factory = TripViewModelFactory(tripDao, username))
    val tripList by tripViewModel.trips.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Dica: pressione e segure para editar uma viagem ou deslize para a esquerda para excluir.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (tripList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma viagem cadastrada.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        items = tripList,
                        key = { trip -> trip.id }  // chave única obrigatória para evitar bugs de animação
                    ) { trip ->
                        TripItem(
                            trip = trip,
                            username = username,
                            navController = navController,
                            onDelete = { tripViewModel.deleteTrip(trip) },
                            onEdit = { navController.navigate("edit_trip/${trip.id}/$username") }
                        )
                    }
                
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TripItem(
    trip: Trip,
    username: String,
    navController: NavController,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {


    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val iconRes = if (trip.type == "Business") R.drawable.ic_business else R.drawable.ic_leisure
    var hasRoteiro by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val roteiroDao = remember { AppDatabase.getDatabase(context).roteiroDao() }

    // Buscar no banco se já existe roteiro para esse trip.id
    LaunchedEffect(trip.id) {
        hasRoteiro = roteiroDao.existsByTripId(trip.id)
    }

    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = if (direction == DismissDirection.EndToStart)
                MaterialTheme.colorScheme.errorContainer else Color.Transparent

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onEdit
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoadMapSugestionButton(trip)
                        if (hasRoteiro) {
                            Button(
                                onClick = {
                                    navController.navigate("RoteiroTripScreen/$username/${trip.id}")
                                }
                            ) {
                                Text("Ver Roteiro")
                            }
                        }
                    }
                }
            }
        }
    )
}




