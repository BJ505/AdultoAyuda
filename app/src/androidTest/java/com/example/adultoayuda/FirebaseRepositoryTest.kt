//package com.example.adultoayuda.Repository
//
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import com.example.adultoayuda.Model.User
//import com.google.firebase.database.FirebaseDatabase
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import kotlin.test.assertFalse
//import kotlin.test.assertTrue
//
//@RunWith(AndroidJUnit4::class)
//class FirebaseRepositoryTest {
//
//    private lateinit var firebaseRepository: FirebaseRepository
//
//    @Before
//    fun setup() {
//        // Obtén el contexto de la aplicación y la instancia de Firebase Database
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        val database = FirebaseDatabase.getInstance()
//        firebaseRepository = FirebaseRepository(database.reference)
//    }
//
//    @After
//    fun teardown() {
//        // Aquí podrías limpiar los datos creados para las pruebas, si es necesario
//    }
//
//    @Test
//    fun registerUser_shouldReturnTrue_whenUserIsRegisteredSuccessfully() = runBlocking {
//        val user = User("test@example.com", "password")
//        val result = firebaseRepository.registerUser(user)
//        assertTrue(result)
//    }
//
//    @Test
//    fun registerUser_shouldReturnFalse_whenRegistrationFails() = runBlocking {
//        // Aquí intentamos registrar un usuario inválido
//        val user = User("", "")  // Datos inválidos
//        val result = firebaseRepository.registerUser(user)
//        assertFalse(result)
//    }
//}
