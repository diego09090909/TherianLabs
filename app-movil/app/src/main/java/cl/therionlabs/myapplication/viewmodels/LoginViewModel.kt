package cl.therionlabs.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.data.ApiException
import cl.therionlabs.myapplication.data.AuthRepository
import cl.therionlabs.myapplication.data.SinConexionException
import cl.therionlabs.myapplication.util.LoginValidator
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
        // 👈 Usamos el validador helper
        when (val resultadoValidacion = LoginValidator.validarFormulario(correo, clave)) {
            is LoginValidator.Resultado.Error -> {
                _uiState.value = LoginUiState.Error(resultadoValidacion.mensaje)
                return
            }
            LoginValidator.Resultado.Valido -> { /* Continúa */ }
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val resultado = authRepository.iniciarSesion(correo.trim().lowercase(), clave)

            resultado.fold(
                onSuccess = { sesion ->
                    _uiState.value = LoginUiState.Success(sesion.correo)
                },
                onFailure = { excepcion ->
                    Log.e("LoginViewModel", "Error de ms-captadores", excepcion)

                    val mensajeParaCaptador = when {
                        excepcion is ApiException && excepcion.codigo == 401 ->
                            "Correo o contraseña incorrectos. Verifica tus datos."

                        excepcion is SinConexionException ->
                            "Sin conexión con el servidor. Revisa tu red e intenta nuevamente."

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