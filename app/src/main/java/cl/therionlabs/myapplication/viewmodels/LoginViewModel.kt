package cl.therionlabs.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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


                    val mensajeParaCaptador = when {
                        excepcion.message?.contains("invalid-credential") == true ||
                                excepcion.message?.contains("user-not-found") == true ||
                                excepcion.message?.contains("wrong-password") == true ->
                            "Correo o contraseña incorrectos. Verifica tus datos."

                        excepcion.message?.contains("network") == true ->
                            "Sin conexión a internet. Revisa tu red e intenta nuevamente."

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