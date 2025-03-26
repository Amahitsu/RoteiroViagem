package com.example.roteiroviagem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roteiroviagem.screens.LoginScreen
import com.example.roteiroviagem.screens.RegisterUser
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoteiroViagemTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Chama a função de navegação
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            // Passa o navController.navigate() como onNavigateTo para LoginScreen
            LoginScreen(onNavigateTo = { route ->
                navController.navigate(route)  // Navega para a tela especificada
            })
        }
        composable("registerUser") {
            RegisterUser(onNavigateTo = { route ->
                navController.navigate(route)  // A tela home
            })
        }
    }
}
