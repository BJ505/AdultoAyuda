package com.example.adultoayuda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun EditUserScreen(navController: NavController, currentUserEmail: String) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Referencia a la base de datos
    val database: DatabaseReference = Firebase.database.reference.child("users")

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        database.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        password = userSnapshot.child("password").getValue(String::class.java) ?: ""
                    }
                } else {
                    errorMessage = "Usuario no encontrado"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = "Error al cargar los datos: ${error.message}"
            }
        })
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Modificar mis datos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de email deshabilitado
        OutlinedTextField(
            value = currentUserEmail,
            onValueChange = {}, // Sin acción ya que es solo lectura
            label = { Text("Email") },
            enabled = false, // Deshabilitado
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                when {
                    password != confirmPassword -> {
                        errorMessage = "Las contraseñas no coinciden"
                    }
                    else -> {
                        // Realiza la actualización del usuario
                        updateUserByUsername(currentUserEmail, database,password) { success ->
                            if (success) {
                                navController.popBackStack()
                            } else {
                                errorMessage = "Error al actualizar los datos"
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar cambios")
        }
    }
}

// Función para actualizar el usuario
fun updateUserByUsername(email: String, database: DatabaseReference, password: String, callback: (Boolean) -> Unit) {
    database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    // Crea un mapa con los datos actualizados
                    val updatedUser = mapOf(
                        "password" to password // Ten en cuenta que esto no es seguro
                    )
                    userSnapshot.ref.updateChildren(updatedUser).addOnCompleteListener { task ->
                        callback(task.isSuccessful)
                    }
                }
            } else {
                callback(false) // Usuario no encontrado
            }
        }

        override fun onCancelled(error: DatabaseError) {
            callback(false) // Error al consultar
        }
    })
}