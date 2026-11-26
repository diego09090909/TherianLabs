package cl.therionlabs.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.data.ApiException
import cl.therionlabs.myapplication.data.AuthRepository
import cl.therionlabs.myapplication.data.SinConexionException
import cl.therionlabs.myapplication.util.RegistroCaptadorValidator
import cl.therionlabs.myapplication.util.Validadores
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

    fun registrarCaptador(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        clave: String,
        confirmarClave: String
    ) {
        val validacion = RegistroCaptadorValidator.validarFormulario(
            nombre, rut, correo, telefono, clave, confirmarClave
        )
        if (validacion is RegistroCaptadorValidator.Resultado.Error) {
            _uiState.value = RegisterUiState.Error(validacion.mensaje)
            return
        }

        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            val resultado = authRepository.registrarCaptador(
                nombre = nombre.trim(),
                rut = Validadores.normalizarRut(rut),
                correo = correo.trim().lowercase(),
                telefono = telefono.replace(" ", ""),
                clave = clave
            )

            resultado.fold(
                onSuccess = { captador ->
                    _uiState.value = RegisterUiState.Success(captador.correo)
                },
                onFailure = { excepcion ->
                    Log.e("RegistroCaptador", "Error de ms-captadores", excepcion)
                    _uiState.value = RegisterUiState.Error(mensajeError(excepcion))
                }
            )
        }
    }

    private fun mensajeError(e: Throwable): String = when (e) {
        // 409: correo o RUT ya registrado; el microservicio dice cuál
        is ApiException -> when (e.codigo) {
            409 -> (e.message ?: "Ya existe una cuenta con esos datos.") + ". Intenta iniciar sesión."
            400 -> e.detalles.firstOrNull() ?: "Hay datos inválidos. Revisa el formulario."
            else -> "Ocurrió un problema al crear la cuenta. Intenta más tarde."
        }
        is SinConexionException -> "Sin conexión con el servidor. Revisa tu red e intenta de nuevo."
        else -> "Ocurrió un problema al crear la cuenta. Intenta más tarde."
    }

    fun resetearEstado() {
        _uiState.value = RegisterUiState.Idle
    }
}
