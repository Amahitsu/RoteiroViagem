package com.example.roteiroviagem.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, username: String?) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Welcome, $username")
                }
            )
        }
    ) { paddingValues ->  // Alterado para utilizar paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)  // Aqui aplica o padding corretamente
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Main Screen Content Here",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
