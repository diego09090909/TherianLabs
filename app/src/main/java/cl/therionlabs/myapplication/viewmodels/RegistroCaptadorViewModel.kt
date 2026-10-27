package cl.therionlabs.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

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

        if (correo.isBlank() || clave.isBlank() || confirmarClave.isBlank()) {
            _uiState.value = RegisterUiState.Error("Todos los campos son obligatorios")
            return
        }

        if (clave != confirmarClave) {
            _uiState.value = RegisterUiState.Error("Las contraseñas no coinciden")
            return
        }

        if (clave.length < 6) {
            _uiState.value = RegisterUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
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

                            Log.e("FirebaseError", "Error real devuelto por Firebase: ", excepcion)


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
}