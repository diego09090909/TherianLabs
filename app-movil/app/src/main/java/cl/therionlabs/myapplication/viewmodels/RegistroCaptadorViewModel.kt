package cl.therionlabs.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.data.AuthRepository
import cl.therionlabs.myapplication.util.RegistroCaptadorValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data class Success(val emailUsuario: String) : RegisterUiState
    data class Error(val mensaje: String) : RegisterUiState
}

class RegisterUserViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registrarCaptador(correo: String, clave: String, confirmarClave: String) {
        // 👈 Usamos el validador helper
        when (val resultadoValidacion = RegistroCaptadorValidator.validarFormulario(correo, clave, confirmarClave)) {
            is RegistroCaptadorValidator.Resultado.Error -> {
                _uiState.value = RegisterUiState.Error(resultadoValidacion.mensaje)
                return
            }
            RegistroCaptadorValidator.Resultado.Valido -> { /* Continúa */ }
        }

        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            val resultado = authRepository.registrarCaptador(correo.trim(), clave)

            resultado.fold(
                onSuccess = { usuario ->
                    if (usuario != null) {
                        _uiState.value = RegisterUiState.Success("Cuenta creada con éxito")
                    } else {
                        _uiState.value = RegisterUiState.Error("No se pudo registrar la cuenta. Intenta nuevamente.")
                    }
                },
                onFailure = { excepcion ->
                    Log.e("FirebaseError", "Error devuelto por Firebase: ", excepcion)

                    val mensajeParaUsuario = when {
                        excepcion.message?.contains("already in use") == true ->
                            "Este correo electrónico ya está registrado. Intenta iniciar sesión."

                        excepcion.message?.contains("network") == true ->
                            "Sin conexión a internet. Revisa tu red e intenta de nuevo."

                        else ->
                            "Ocurrió un problema al crear la cuenta. Intenta más tarde."
                    }

                    _uiState.value = RegisterUiState.Error(mensajeParaUsuario)
                }
            )
        }
    }

    fun resetearEstado() {
        _uiState.value = RegisterUiState.Idle
    }
}