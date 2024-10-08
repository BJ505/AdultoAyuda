package com.example.adultoayuda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adultoayuda.Repository.FirebaseRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Obtén el CoroutineScope para lanzar corutinas
    val coroutineScope = rememberCoroutineScope()
    val firebaseRepository = FirebaseRepository() // Instancia del repositorio

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_aa),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = email.isEmpty()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    painterResource(id = R.drawable.baseline_visibility_24)
                else
                    painterResource(id = R.drawable.baseline_visibility_off_24)

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = image, contentDescription = null)
                }
            },
            isError = password.isEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val isSuccess = firebaseRepository.authenticateUser(email, password)
                        if (isSuccess) {
                            navController.navigate("home/$email")
                        } else {
                            errorMessage = "Nombre de usuario o contraseña inválidos"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error de conexión"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("register") }
        ) {
            Text("Crear cuenta", fontSize = 24.sp)
        }

        TextButton(
            onClick = { navController.navigate("forgot_password") }
        ) {
            Text("Recuperar contraseña", fontSize = 24.sp)
        }
    }
}