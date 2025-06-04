package com.example.roteiroviagem.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roteiroviagem.R
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.viewmodels.TripViewModel
import com.example.roteiroviagem.viewmodels.TripViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(navController: NavController, username: String, existingTrip: Trip? = null) {
    val ctx = LocalContext.current
    val tripDao = AppDatabase.getDatabase(ctx).tripDao()
    val tripViewModel: TripViewModel = viewModel(factory = TripViewModelFactory(tripDao, username))

    var destination by remember { mutableStateOf(TextFieldValue(existingTrip?.destination ?: "")) }
    var startDate by remember { mutableStateOf(existingTrip?.startDate?.let { Date(it) }) }
    var endDate by remember { mutableStateOf(existingTrip?.endDate?.let { Date(it) }) }
    var budget by remember { mutableStateOf(existingTrip?.orcamento?.toString() ?: "") }
    var selectedType by remember { mutableStateOf(existingTrip?.type ?: "Business") }

    fun formatDate(date: Date?, defaultText: String): String {
        return date?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
        } ?: defaultText
    }

    fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            ctx,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (existingTrip == null) "Nova Viagem" else "Editar Viagem",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("MainScreen/$username") {
                            popUpTo("MainScreen/$username") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Text("Tipo de Viagem:")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val iconBgColor = MaterialTheme.colorScheme.primary

                IconButton(
                    onClick = { selectedType = "Business" },
                    modifier = Modifier
                        .size(72.dp)
                        .background(iconBgColor, RoundedCornerShape(12.dp))
                        .border(
                            width = if (selectedType == "Business") 2.dp else 0.dp,
                            color = if (selectedType == "Business") Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_business),
                        contentDescription = "Negócio",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = { selectedType = "Leisure" },
                    modifier = Modifier
                        .size(72.dp)
                        .background(iconBgColor, RoundedCornerShape(12.dp))
                        .border(
                            width = if (selectedType == "Leisure") 2.dp else 0.dp,
                            color = if (selectedType == "Leisure") Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_leisure),
                        contentDescription = "Lazer",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Button(
                onClick = {
                    showDatePicker { date ->
                        if (endDate != null && date.after(endDate)) {
                            Toast.makeText(ctx, "Data de ida não pode ser depois da volta.", Toast.LENGTH_SHORT).show()
                        } else {
                            startDate = date
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(formatDate(startDate, "Selecione a data de ida"), color = MaterialTheme.colorScheme.onPrimary)
            }

            Button(
                onClick = {
                    showDatePicker { date ->
                        if (startDate != null && date.before(startDate)) {
                            Toast.makeText(ctx, "Data de volta não pode ser antes da ida.", Toast.LENGTH_SHORT).show()
                        } else {
                            endDate = date
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(formatDate(endDate, "Selecione a data de volta"), color = MaterialTheme.colorScheme.onPrimary)
            }

            OutlinedTextField(
                value = budget,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d*"))) {
                        budget = newValue
                    }
                },
                label = { Text("Orçamento") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("R$ ") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Button(
                onClick = {
                    val orcamentoValue = budget.toDoubleOrNull()
                    val validDates = startDate != null && endDate != null && !startDate!!.after(endDate!!)

                    when {
                        destination.text.isBlank() -> Toast.makeText(ctx, "Informe o destino.", Toast.LENGTH_SHORT).show()
                        startDate == null -> Toast.makeText(ctx, "Selecione a data de ida.", Toast.LENGTH_SHORT).show()
                        endDate == null -> Toast.makeText(ctx, "Selecione a data de volta.", Toast.LENGTH_SHORT).show()
                        !validDates -> Toast.makeText(ctx, "Datas inválidas.", Toast.LENGTH_SHORT).show()
                        orcamentoValue == null || orcamentoValue <= 0 -> Toast.makeText(ctx, "Informe um orçamento válido.", Toast.LENGTH_SHORT).show()
                        else -> {
                            val trip = Trip(
                                id = existingTrip?.id ?: 0,
                                destination = destination.text,
                                startDate = startDate!!.time,
                                endDate = endDate!!.time,
                                orcamento = orcamentoValue,
                                type = selectedType,
                                username = username
                            )

                            if (existingTrip != null) {
                                tripViewModel.updateTrip(trip)
                                Toast.makeText(ctx, "Viagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                            } else {
                                tripViewModel.addTrip(trip)
                                Toast.makeText(ctx, "Viagem salva com sucesso!", Toast.LENGTH_SHORT).show()
                            }

                            navController.navigate("MainScreen/$username") {
                                popUpTo("MainScreen/$username") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (existingTrip == null) "Confirmar" else "Atualizar", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
