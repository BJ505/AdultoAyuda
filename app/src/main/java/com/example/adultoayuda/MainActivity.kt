package com.example.adultoayuda

import LocationPickerScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adultoayuda.ui.theme.AdultoAyudaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdultoAyudaTheme{
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdultoAyudaApp(navController, Modifier.padding(innerPadding))
                }
            }

        }
    }
}

@Composable
fun AdultoAyudaApp(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("home/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            HomeScreen(navController, email)
        }
        // Agrega la ruta para LocationPickerScreen
        composable("location_picker/{email}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("email") ?: ""
            LocationPickerScreen(navController, username)
        }

        composable("edit_user/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            EditUserScreen(navController, username)
        }

        composable("user_list/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserListScreen(username)
        }
    }
}

