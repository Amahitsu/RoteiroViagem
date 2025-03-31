package com.example.roteiroviagem.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roteiroviagem.R  // Certifique-se de importar corretamente o R
import com.example.roteiroviagem.components.MyTextField
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme
import com.example.roteiroviagem.viewmodels.LoginScreenViewModel

@Composable
fun LoginScreen(onNavigateTo: (String) -> Unit) {  // Adicionando o parâmetro de navegação
    val loginScreenViewModel: LoginScreenViewModel = viewModel()

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            LoginScreenFields(
                loginScreenViewModel,
                onNavigateTo
            )  // Passando o parâmetro para a próxima função
        }
    }
}

@Composable
fun LoginScreenFields(
    loginScreenViewModel: LoginScreenViewModel,
    onNavigateTo: (String) -> Unit
) {
    val loginRegisterUser = loginScreenViewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        // Exibir o logotipo no topo
        Image(
            painter = painterResource(id = R.drawable.logotipo),
            contentDescription = "Logotipo",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(600.dp))
        )

        MyTextField(
            label = "Usuário",
            value = loginRegisterUser.value.user,
            onValueChange = { loginScreenViewModel.onUserChange(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MyTextField(
            label = "Senha",
            value = loginRegisterUser.value.password,
            onValueChange = { loginScreenViewModel.onPasswordChange(it) },
            visualTransformation = PasswordVisualTransformation(),
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))



        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                // Navegar para "o registro de usuário" para cadastro de novo login
                onNavigateTo("MainScreen")
            }) {
            Text(
                text = "Login",
                fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Já tem uma conta? Faça seu registro",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                onNavigateTo("RegisterUser")
            })

    }
}


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewLoginScreen() {
    RoteiroViagemTheme {
        LoginScreen(onNavigateTo = {})
    }
}

