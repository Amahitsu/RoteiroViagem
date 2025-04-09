package com.example.roteiroviagem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roteiroviagem.screens.LoginScreen
import com.example.roteiroviagem.screens.MainScreen
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
                    MyApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "RouterTravel") }) },
        bottomBar = {
            val backStack = navController.currentBackStackEntryAsState()
            val currentDestination = backStack.value?.destination?.route

            // Exibe a BottomNavigation apenas nas telas desejadas
            if (currentDestination in listOf("MainScreen", "Profile", "About")) {
                BottomNavigation {
                    BottomNavigationItem(
                        selected = currentDestination == "Profile",
                        onClick = { navController.navigate("Profile") },
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") }
                    )
                    BottomNavigationItem(
                        selected = currentDestination == "MainScreen",
                        onClick = { navController.navigate("MainScreen") },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Main Screen") }
                    )
                    BottomNavigationItem(
                        selected = currentDestination == "About",
                        onClick = { navController.navigate("About") },
                        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "About") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Login", // Começa na tela de Login
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Login") { LoginScreen(navController = navController) }
            composable("MainScreen/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username")
                if (username != null) {
                    MainScreen(navController = navController, username = username)
                }
            }
            composable("RegisterUser") {
                RegisterUser(onNavigateTo = { route ->
                    navController.navigate(route)
                })
            }
        }
    }
}
