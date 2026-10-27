package cl.therionlabs.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.data.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// SOLO PARA PRUEBAS: poner en false (o borrar) antes de entregar
private const val MODO_PRUEBA = true
private const val CORREO_ATAJO = "test@duoc.cl"

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val emailUsuario: String) : LoginUiState
    data class Error(val mensaje: String) : LoginUiState
}

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarSesion(correo: String, clave: String) {
        val correoLimpio = correo.trim()

        if (correoLimpio.isBlank() || clave.isBlank()) {
            _uiState.value = LoginUiState.Error("Por favor, ingresa tu correo y contraseña")
            return
        }

        // ATAJO DE PRUEBAS: entrar sin Firebase
        if (MODO_PRUEBA && correoLimpio == CORREO_ATAJO) {
            _uiState.value = LoginUiState.Success(CORREO_ATAJO)
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val resultado = authRepository.iniciarSesion(correoLimpio, clave)

            resultado.fold(
                onSuccess = { usuario ->
                    if (usuario != null) {
                        _uiState.value = LoginUiState.Success(usuario.email ?: correoLimpio)
                    } else {
                        _uiState.value = LoginUiState.Error("No se pudo iniciar sesión. Intenta nuevamente.")
                    }
                },
                onFailure = { excepcion ->
                    Log.e("LoginViewModel", "Error en Firebase Auth", excepcion)

                    val codigo = (excepcion as? FirebaseAuthException)?.errorCode

                    val mensajeParaCaptador = when {
                        codigo == "ERROR_INVALID_CREDENTIAL" ||
                                codigo == "ERROR_USER_NOT_FOUND" ||
                                codigo == "ERROR_WRONG_PASSWORD" ||
                                excepcion is FirebaseAuthInvalidCredentialsException ||
                                excepcion is FirebaseAuthInvalidUserException ->
                            "Correo o contraseña incorrectos. Verifica tus datos."

                        excepcion is FirebaseNetworkException ->
                            "Sin conexión a internet. Revisa tu red e intenta nuevamente."

                        MODO_PRUEBA ->
                            "Error: ${excepcion.javaClass.simpleName} - ${excepcion.message}"

                        else ->
                            "Ocurrió un problema al iniciar sesión. Intenta más tarde."
                    }

                    _uiState.value = LoginUiState.Error(mensajeParaCaptador)
                }
            )
        }
    }

    fun resetearEstado() {
        _uiState.value = LoginUiState.Idle
    }
}