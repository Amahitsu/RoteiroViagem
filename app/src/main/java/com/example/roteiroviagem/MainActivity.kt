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
import com.example.roteiroviagem.screens.MainScreen
import com.example.roteiroviagem.screens.RegisterUser
import com.example.roteiroviagem.ui.theme.RoteiroViagemTheme

//import android.os.Bundle
//import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
//import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState


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
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "My app")
                }
            )
        },
        bottomBar = {
            BottomNavigation {
                val backStack = navController.currentBackStackEntryAsState()
                val currentDestination = backStack.value?.destination
                BottomNavigationItem(
                    selected =
                    currentDestination?.hierarchy?.any {
                        it.route == "MainScreen"
                    } == true ,
                    onClick = { navController.navigate("MainScreen") },
                    icon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Main Screen")
                    }
                )
                BottomNavigationItem(
                    selected =
                    currentDestination?.hierarchy?.any {
                        it.route == "Profile"
                    } == true ,
                    onClick = { navController.navigate("Profile") },
                    icon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    }
                )
                BottomNavigationItem(
                    selected =
                    currentDestination?.hierarchy?.any {
                        it.route == "About"
                    } == true ,
                    onClick = { navController.navigate("About") },
                    icon = {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "About")
                    }
                )

            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            NavHost(navController = navController, startDestination = "Login") {
                composable("MainScreen") {
                    MainScreen()
                }
                composable("Login") {
                    LoginScreen(onNavigateTo = {})
                }
                composable("RegisterUser"    ) {
                    RegisterUser(onNavigateTo = {})
                }
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoteiroViagemTheme {
        MyApp()
    }
}