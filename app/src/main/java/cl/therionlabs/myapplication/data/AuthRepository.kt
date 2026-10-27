package cl.therionlabs.myapplication.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val usuarioActual: FirebaseUser?
        get() = firebaseAuth.currentUser

    // Ahora acepta el nombre para guardarlo en el perfil de Firebase al crear la cuenta
    suspend fun registrarCaptador(
        correo: String,
        clave: String,
        nombre: String? = null
    ): Result<FirebaseUser?> {
        return try {
            val resultado = firebaseAuth.createUserWithEmailAndPassword(correo, clave).await()
            val usuario = resultado.user
            if (usuario != null && !nombre.isNullOrBlank()) {
                usuario.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(nombre.trim()).build()
                ).await()
            }
            Result.success(usuario)
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

    suspend fun actualizarNombre(nombre: String): Result<Unit> {
        return try {
            val usuario = firebaseAuth.currentUser
                ?: return Result.failure(IllegalStateException("Sin sesión"))
            usuario.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(nombre).build()
            ).await()
            usuario.reload().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Firebase exige confirmar la clave actual antes de cambiarla (reautenticación)
    suspend fun cambiarClave(claveActual: String, claveNueva: String): Result<Unit> {
        return try {
            val usuario = firebaseAuth.currentUser
                ?: return Result.failure(IllegalStateException("Sin sesión"))
            val correo = usuario.email
                ?: return Result.failure(IllegalStateException("Sin correo"))
            usuario.reauthenticate(EmailAuthProvider.getCredential(correo, claveActual)).await()
            usuario.updatePassword(claveNueva).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        firebaseAuth.signOut()
    }
}