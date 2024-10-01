package com.example.adultoayuda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.adultoayuda.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun UserListScreen(currentUserEmail: String) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val database: DatabaseReference = Firebase.database.reference.child("users")

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users = snapshot.children.mapNotNull {
                    it.getValue(User::class.java)?.copy(password = "***") // Ocultar contraseña
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                error = "Error al cargar usuarios: ${databaseError.message}"
            }
        }
        database.addValueEventListener(listener)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Usuarios Registrados",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            users.isEmpty() -> Text("No hay usuarios registrados")
            else -> UserList(users, currentUserEmail, database, onDeleteUser = { deletedEmail ->
                users = users.filter { it.email != deletedEmail }  // Filtrar el usuario eliminado
            })
        }
    }
}

@Composable
fun UserList(
    users: List<User>,
    currentUserEmail: String,
    database: DatabaseReference,
    onDeleteUser: (String) -> Unit
) {
    LazyColumn {
        items(users) { user ->
            UserCard(user, currentUserEmail, database, onDeleteUser)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun UserCard(
    user: User,
    currentUserEmail: String,
    database: DatabaseReference,
    onDeleteUser: (String) -> Unit
) {
    val isAdmin = currentUserEmail == "bjaramillo@duoc.cl"  // Verificar si el usuario es el admin
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            // Mostrar botón de eliminar solo si el usuario es admin
            if (isAdmin) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        deletedEmail(user.email, database) { success ->
                            if (success) {
                                onDeleteUser(user.email)  // Eliminar usuario de la lista
                            } else {
                                // Manejar el error (opcional)
                            }
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

fun deletedEmail(email: String, database: DatabaseReference, callback: (Boolean) -> Unit) {
    // Realiza una consulta para buscar al usuario basado en su nombre de usuario
    database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                // Recorre los resultados (aunque debería haber solo uno)
                for (userSnapshot in snapshot.children) {
                    // Elimina el nodo completo del usuario
                    userSnapshot.ref.removeValue().addOnCompleteListener { task ->
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