package cl.therionlabs.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import cl.therionlabs.myapplication.model.RegistroErrores
import cl.therionlabs.myapplication.model.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel : ViewModel() {

    // Canal privado de actualización
    private val _estado = MutableStateFlow(RegistroUiState())

    // Canal público de solo lectura para la vista
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null) // Limpia el error al escribir
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = null)
            )
        }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update {
            it.copy(
                aceptaTerminos = valor,
                errores = it.errores.copy(aceptaTerminos = null)
            )
        }
    }

    fun validar(): Boolean {
        val actual = estado.value
        val errores = RegistroErrores(
            nombre = if (actual.nombre.isBlank()) "El nombre es obligatorio" else null,
            correo = when {
                actual.correo.isBlank() -> "El correo es obligatorio"
                !actual.correo.contains("@") -> "El correo debe incluir @"
                else -> null
            },
            aceptaTerminos = if (!actual.aceptaTerminos) "Debes aceptar los términos" else null
        )

        _estado.update { it.copy(errores = errores) }
        return errores.nombre == null && errores.correo == null && errores.aceptaTerminos == null
    }
}