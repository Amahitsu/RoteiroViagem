package com.example.roteiroviagem.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roteiroviagem.R
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.viewmodels.LoginScreenViewModel
import com.example.roteiroviagem.viewmodels.LoginViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val registerUserDao = AppDatabase.getDatabase(context).userDao()
    val loginViewModel: LoginScreenViewModel = viewModel(factory = LoginViewModelFactory(registerUserDao))

    val state = loginViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logotipo),
                contentDescription = "Logotipo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = state.value.user,
                onValueChange = { loginViewModel.onUserChange(it) },
                label = { Text("Usuário") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.value.password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { loginViewModel.login() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("RegisterUser") }) {
                Text("Não tem uma conta? Faça seu registro",
                    fontSize = 15.sp)
            }
        }

        // Se login OK, navega para menu/{username}
        if (state.value.isLoggedIn) {
            LaunchedEffect(Unit) {
                navController.navigate("MainScreen/${state.value.user}") {
                    popUpTo("Login") { inclusive = true }
                }
            }
        }

        // Exibe Snackbar se erro
        if (state.value.errorMessage.isNotBlank()) {
            LaunchedEffect(state.value.errorMessage) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(state.value.errorMessage)
                    loginViewModel.cleanErrorMessage()
                }
            }
        }
    }
}
