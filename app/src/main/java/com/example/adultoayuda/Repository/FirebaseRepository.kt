package com.example.adultoayuda.Repository

import android.util.Log
import com.example.adultoayuda.Model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

public open class FirebaseRepository {
    protected open val database: DatabaseReference = Firebase.database.reference

    open suspend fun registerUser(user: User): Boolean {
        return try {
            database.child("users").push().setValue(user).await()
            true // Si se completa correctamente
        } catch (e: Exception) {
            false // En caso de error
        }
    }

    open suspend fun authenticateUser(email: String, password: String): Boolean {
        val snapshot = database.child("users").get().await()
        for (userSnapshot in snapshot.children) {
            val user = userSnapshot.getValue(User::class.java)
            if (user != null && user.email == email && user.password == password) {
                return true
            }
        }
        return false
    }


    open suspend fun saveLocationByEmail(email: String, locationData: Map<String, Double>): Boolean {
        // Loguea los datos que llegan al método
        Log.d("FirebaseRepository", "Datos recibidos - Username: $email, LocationData: $locationData")

        return try {
            val snapshot = database.child("users").orderByChild("email").equalTo(email).get().await()

            if (snapshot.exists()) {
                // Recorre los resultados, debería haber solo uno
                for (userSnapshot in snapshot.children) {
                    // Guarda la ubicación dentro del nodo "location"
                    userSnapshot.ref.child("location").updateChildren(locationData).await()
                    Log.d("FirebaseRepository", "Ubicación guardada para el usuario: $email")
                }
                true // Operación exitosa
            } else {
                Log.e("FirebaseRepository", "Usuario no encontrado: $email")
                false // Usuario no encontrado
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error al guardar la ubicación: ${e.localizedMessage}", e)
            false // Error al realizar la operación
        }
    }

}