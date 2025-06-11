package com.example.roteiroviagem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.roteiroviagem.api.GeminiService
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.screens.*
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme
import com.example.roteiroviagem.viewmodel.RoteiroViewModel
import com.example.roteiroviagem.data.repository.RoteiroRepository
import com.example.roteiroviagem.viewmodels.RoteiroViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoteiroViagemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
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
    var username by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    // Banco e DAO
    val database = remember { AppDatabase.getDatabase(context) }
    val tripDao = remember { database.tripDao() }
    val roteiroDao = remember { database.roteiroDao() }

    val backStack by navController.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination?.route

    val showBottomBar = currentDestination?.startsWith("MainScreen") == true ||
            currentDestination?.startsWith("RoteiroTripScreen") == true ||
            currentDestination?.startsWith("About") == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Viagens", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    if (currentDestination != "Login" && currentDestination != "RegisterUser") {
                        TextButton(onClick = {
                            navController.navigate("Login") {
                                popUpTo("MainScreen/$username") { inclusive = true }
                                launchSingleTop = true
                            }
                        }) {
                            Text("Sair", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    BottomNavigationItem(
                        selected = currentDestination?.startsWith("ListaRoteirosScreen") == true,
                        onClick = {
                            if (username.isNotBlank()) {
                                navController.navigate("ListaRoteirosScreen/$username") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Backpack,
                                contentDescription = "Roteiros",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )

                    BottomNavigationItem(
                        selected = currentDestination?.startsWith("MainScreen") == true,
                        onClick = {
                            if (username.isNotBlank()) {
                                navController.navigate("MainScreen/$username") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Tela Principal",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )

                    BottomNavigationItem(
                        selected = currentDestination?.startsWith("About") == true,
                        onClick = {
                            if (username.isNotBlank()) {
                                navController.navigate("About/$username") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Sobre",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar && username.isNotBlank()) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_trip/$username") },
                    containerColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Adicionar Viagem")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Login") {
                LoginScreen(navController = navController)
            }
            composable("MainScreen/{username}") { backStackEntry ->
                val userArg = backStackEntry.arguments?.getString("username") ?: ""
                if (username != userArg) username = userArg
                MainScreen(navController = navController, username = userArg)
            }
            composable("About/{username}") { backStackEntry ->
                val userArg = backStackEntry.arguments?.getString("username") ?: ""
                if (username != userArg) username = userArg
                AboutScreen(navController = navController, username = userArg)
            }
            composable("RegisterUser") {
                RegisterUser(onNavigateTo = { route ->
                    navController.navigate(route)
                })
            }
            composable("add_trip/{username}") { backStackEntry ->
                val userArg = backStackEntry.arguments?.getString("username") ?: ""
                AddTripScreen(navController = navController, username = userArg)
            }
            composable("edit_trip/{tripId}/{username}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull()
                val userArg = backStackEntry.arguments?.getString("username") ?: ""

                val tripState = produceState<Trip?>(initialValue = null, tripId) {
                    value = tripId?.let { tripDao.getById(it) }
                }

                tripState.value?.let { trip ->
                    AddTripScreen(
                        navController = navController,
                        username = userArg,
                        existingTrip = trip
                    )
                }
            }

            composable("ListaRoteirosScreen/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                ListaRoteirosScreen(
                    username = username,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("RoteiroTripScreen/{username}/{tripId}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                val tripId = backStackEntry.arguments?.getString("tripId")?.toLongOrNull() ?: return@composable

                RoteiroTripScreen(
                    username = username,
                    tripId = tripId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}