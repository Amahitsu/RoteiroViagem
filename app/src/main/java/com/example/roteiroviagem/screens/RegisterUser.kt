package com.example.roteiroviagem.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
                .padding(it)
                .padding(30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {

        MyTextField(
            label = "Insira o usuário",
            value = registerUser.value.userRegister,
            onValueChange = { registerUserViewModel.onRegisterUser(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MyTextField(
            label = "Insira a senha",
            value = registerUser.value.emailRegister,
            onValueChange = { registerUserViewModel.onRegisterEmail(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MyTextField(
            label = "Insira a senha",
            value = registerUser.value.passwordRegister,
            onValueChange = { registerUserViewModel.onRegisterEmail(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MyTextField(
            label = "Confirme a senha",
            value = registerUser.value.confirmPassword,
            onValueChange = { registerUserViewModel.onConfirmPassword(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))


        Row() {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    // Navegar para "home" após o login
                    onNavigateTo("RegisterUser")
                }) {
                Text(text = "Registrar Usuário")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(modifier = Modifier.weight(1f),
                onClick = {
                    // Navegar para "o registro de usuário" para cadastro de novo login
                    onNavigateTo("RegisterUser")
                }) {
                Text(text = "Registrar Usuário")
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