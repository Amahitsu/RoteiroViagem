package com.example.roteiroviagem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roteiroviagem.database.AppDatabase
import com.example.roteiroviagem.entity.Trip
import com.example.roteiroviagem.screens.AboutScreen
import com.example.roteiroviagem.screens.AddTripScreen
import com.example.roteiroviagem.screens.LoginScreen
import com.example.roteiroviagem.screens.MainScreen
import com.example.roteiroviagem.screens.RegisterUser
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme

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

    val backStack = navController.currentBackStackEntryAsState()
    val currentDestination = backStack.value?.destination?.route

    val showBottomBar = currentDestination?.startsWith("MainScreen") == true ||
            currentDestination?.startsWith("Profile") == true ||
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
                                popUpTo("MainScreen/{$username}") { inclusive = true }
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
                        selected = currentDestination?.startsWith("Profile") == true,
                        onClick = { navController.navigate("Profile") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                    BottomNavigationItem(
                        selected = currentDestination?.startsWith("MainScreen") == true,
                        onClick = {
                            if (username.isNotBlank()) {
                                navController.navigate("MainScreen/$username")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Main Screen",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                    BottomNavigationItem(
                        selected = currentDestination == "About",
                        onClick = { navController.navigate("About/$username") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar && username.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { navController.navigate("add_trip/$username") },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Adicionar Viagem")
                    }
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
                val argUsername = backStackEntry.arguments?.getString("username")
                if (argUsername != null) {
                    username = argUsername
                    MainScreen(navController = navController, username = username)
                }
            }
            composable("About/{username}") { backStackEntry ->
                val usernameArg = backStackEntry.arguments?.getString("username") ?: ""
                username = usernameArg // <- Aqui é onde corrigimos
                AboutScreen(navController = navController, username = usernameArg)
            }
            composable("RegisterUser") {
                RegisterUser(onNavigateTo = { route ->
                    navController.navigate(route)
                })
            }
            composable("add_trip/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                AddTripScreen(navController = navController, username = username)
            }
            composable("edit_trip/{tripId}/{username}") { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull()
                val username = backStackEntry.arguments?.getString("username") ?: ""
                val context = LocalContext.current
                val tripDao = AppDatabase.getDatabase(context).tripDao()
                var trip by remember { mutableStateOf<Trip?>(null) }

                LaunchedEffect(tripId) {
                    trip = tripId?.let { tripDao.getById(it) }
                }

                trip?.let {
                    AddTripScreen(
                        navController = navController,
                        username = username,
                        existingTrip = it
                    )
                }
            }
        }
    }
}