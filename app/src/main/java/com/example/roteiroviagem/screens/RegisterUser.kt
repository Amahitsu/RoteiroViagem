package com.example.roteiroviagem.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roteiroviagem.components.MyTextField
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme
import com.example.roteiroviagem.viewmodels.RegisterUserViewModel
import com.example.roteiroviagem.components.EmailValidator
import com.example.roteiroviagem.components.ErrorDialog
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.viewmodels.RegisterUserViewModelFactory

@Composable
fun RegisterUser(onNavigateTo: (String) -> Unit) {  // Adicionando o parâmetro de navegação
    val ctx = LocalContext.current
    val userDao = AppDatabase.getDatabase(ctx).userDao()
    val registerUserViewModel: RegisterUserViewModel = viewModel(
        factory = RegisterUserViewModelFactory(userDao)
    )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserField(
    registerUserViewModel: RegisterUserViewModel,
    onNavigateTo: (String) -> Unit
) {
    var registerUser = registerUserViewModel.uiState.collectAsState()
    val ctx = LocalContext.current
    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar o alerta
    LaunchedEffect(registerUser.value.passwordError) {
        showDialog = registerUser.value.passwordError != null &&
                registerUser.value.confirmPassword.isNotBlank() &&
                registerUser.value.passwordRegister.isNotBlank()
    }


    // Mostra o alerta sempre que o erro mudar
    LaunchedEffect(registerUser.value.passwordError) {
        showDialog = registerUser.value.passwordError != null
    }
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

            //registra o usuário
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Alinha o texto à esquerda
            ) {
                Text(
                    text = "Insira o Usuário",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            MyTextField(
                label = "Usuário",
                value = registerUser.value.userRegister,
                onValueChange = { registerUserViewModel.onRegisterUser(it) }
            )

            Spacer(modifier = Modifier.height(5.dp))

            //Entrada de senha
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Alinha o texto à esquerda
            ) {
                Text(
                    text = "Insira a senha",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            MyTextField(
                label = "senha",
                value = registerUser.value.passwordRegister,
                onValueChange = { registerUserViewModel.onRegisterPassword(it) },
                visualTransformation = PasswordVisualTransformation(),
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(5.dp))

            //Confirmação de senha
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Alinha o texto à esquerda
            ) {
                Text(
                    text = "Confirme a senha",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            MyTextField(
                label = "Confirme a senha",
                value = registerUser.value.confirmPassword,
                onValueChange = { registerUserViewModel.onConfirmPassword(it) },
                visualTransformation = PasswordVisualTransformation(),
                isPassword = true,
                modifier = Modifier.onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        registerUserViewModel.onConfirmPasswordFocusLost()
                    }
                }
            )

            Spacer(modifier = Modifier.height(5.dp))

            //Entrada de nome
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Alinha o texto à esquerda
            ) {
                Text(
                    text = "Insira seu nome",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            MyTextField(
                label = "Nome",
                value = registerUser.value.nameRegister,
                onValueChange = { registerUserViewModel.onRegisterName(it) }
            )

            Spacer(modifier = Modifier.height(5.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Alinha o texto à esquerda
            ) {
                Text(
                    text = "Insira o email",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            MyTextField(
                label = "Email",
                value = registerUser.value.emailRegister,
                onValueChange = { registerUserViewModel.onRegisterEmail(it) },
                errorMessage = registerUser.value.emailError
            )


            Spacer(modifier = Modifier.height(3.dp))



            Button(
                onClick = { onNavigateTo("Login")
                          registerUserViewModel.register()},
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = registerUser.value.isFormValid
            ) {
                Text(text = "Registrar Usuário", fontSize = 18.sp)
            }

            if(registerUser.value.errorMessage.isNotBlank()){
                ErrorDialog(
                    error = registerUser.value.errorMessage,
                    onDismissRequest = {
                        registerUserViewModel.cleanDisplayValues()
                    }
                )
            }


            Button(
                onClick = { onNavigateTo("Login") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Voltar", fontSize = 18.sp)
            }

            // Alerta automático quando as senhas não coincidem
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Erro") },
                    text = { Text("As senhas não coincidem!") },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                )
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