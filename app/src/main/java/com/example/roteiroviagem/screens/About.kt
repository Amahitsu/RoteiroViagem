package com.example.roteiroviagem.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController, username: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Bem-vindo ao RoteiroViagem!",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = """
                Este aplicativo foi desenvolvido para ajudar você a planejar, organizar e registrar suas viagens de forma prática e eficiente.

                Com ele, você pode adicionar destinos, salvar detalhes importantes sobre cada roteiro, e ter controle sobre suas experiências de viagem em um só lugar.

                Nosso objetivo é tornar suas viagens mais organizadas e inesquecíveis.

                Versão: 1.0.0
            """.trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
