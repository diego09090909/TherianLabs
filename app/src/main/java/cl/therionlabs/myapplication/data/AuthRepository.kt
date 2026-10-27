package cl.therionlabs.myapplication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    val usuarioActual: FirebaseUser?
        get() = firebaseAuth.currentUser


    suspend fun registrarCaptador(correo: String, clave: String): Result<FirebaseUser?> {
        return try {
            val resultado = firebaseAuth.createUserWithEmailAndPassword(correo, clave).await()
            Result.success(resultado.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun iniciarSesion(correo: String, clave: String): Result<FirebaseUser?> {
        return try {
            val resultado = firebaseAuth.signInWithEmailAndPassword(correo, clave).await()
            Result.success(resultado.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun cerrarSesion() {
        firebaseAuth.signOut()
    }
}