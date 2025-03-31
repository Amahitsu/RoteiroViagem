package com.example.roteiroviagem.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roteiroviagem.components.MyTextField
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme
import com.example.roteiroviagem.viewmodels.RegisterUserViewModel

@Composable
fun RegisterUser(onNavigateTo: (String) -> Unit) {  // Adicionando o parâmetro de navegação
    val registerUserViewModel: RegisterUserViewModel = viewModel()

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it),

            horizontalAlignment = Alignment.CenterHorizontally, // Centraliza horizontalmente
            verticalArrangement = Arrangement.Center // Centraliza verticalmente
        ) {
            RegisterUserField(
                registerUserViewModel,
                onNavigateTo
            )
        }
    }
}

@Composable
fun RegisterUserField(
    registerUserViewModel: RegisterUserViewModel,
    onNavigateTo: (String) -> Unit
) {
    val registerUser = registerUserViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(), // Ocupa toda a tela
        horizontalAlignment = Alignment.CenterHorizontally, // Centraliza horizontalmente
        verticalArrangement = Arrangement.Center // Centraliza verticalmente
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f), // Mantém o formulário mais centralizado
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyTextField(
                label = "Insira o usuário",
                value = registerUser.value.userRegister,
                onValueChange = { registerUserViewModel.onRegisterUser(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            MyTextField(
                label = "Insira o email",
                value = registerUser.value.emailRegister,
                onValueChange = { registerUserViewModel.onRegisterEmail(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            MyTextField(
                label = "Insira a senha",
                value = registerUser.value.passwordRegister,
                onValueChange = { registerUserViewModel.onRegisterPassword(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            MyTextField(
                label = "Confirme a senha",
                value = registerUser.value.confirmPassword,
                onValueChange = { registerUserViewModel.onConfirmPassword(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateTo("MainSreen") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Registrar Usuário")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onNavigateTo("Login") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Voltar")
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewRegisterScreen() {
    RoteiroViagemTheme {
        RegisterUser(onNavigateTo = {})
    }
}