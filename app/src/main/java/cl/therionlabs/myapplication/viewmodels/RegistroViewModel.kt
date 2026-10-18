package cl.therionlabs.myapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.CapturaApp
import cl.therionlabs.myapplication.data.CryptoManager
import cl.therionlabs.myapplication.data.Prospecto
import cl.therionlabs.myapplication.model.RegistroErrores
import cl.therionlabs.myapplication.model.ResumenProspecto
import cl.therionlabs.myapplication.model.RegistroUiState
import cl.therionlabs.myapplication.util.Validadores
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MSG_RUT_INVALIDO = "El RUT no es válido. Revisa el dígito verificador."
private const val MSG_CORREO_INVALIDO = "Ingresa un correo válido, por ejemplo nombre@correo.cl."

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as CapturaApp).repository

    private val _estado = MutableStateFlow(RegistroUiState())

    // Canal público de solo lectura para la vista
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    private val _mensajes = MutableSharedFlow<String>()
    val mensajes: SharedFlow<String> = _mensajes.asSharedFlow()

    private val _guardados = MutableSharedFlow<ResumenProspecto>()
    val guardados: SharedFlow<ResumenProspecto> = _guardados.asSharedFlow()

    val prospectos = repo.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendientes = repo.contarPendientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onEventoChange(valor: String) {
        _estado.update { it.copy(eventoId = valor) }
    }

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null) // Limpia el error al escribir
            )
        }
    }

    fun onRutChange(valor: String) {
        val formateado = Validadores.formatearRutEnVivo(valor)
        val completo = formateado.substringAfter('-',"").length == 1
        _estado.update {
            it.copy(
                rut = formateado,
                errores = it.errores.copy(
                    rut = if (completo && !Validadores.rutValido(formateado)) MSG_RUT_INVALIDO else null
                )
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(
                    correo = if (valor.isNotBlank() && !Validadores.correoValido(valor)) MSG_CORREO_INVALIDO else null
                )
            )
        }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update {
            it.copy(
                telefono = valor,
                errores = it.errores.copy(
                    telefono = if (valor.isNotBlank() && !Validadores.telefonoValido(valor)) "Use formato 9XXXXXXXX" else null
                )
            )
        }
    }

    fun onColegioChange(valor: String) {
        _estado.update { it.copy(colegio = valor, errores = it.errores.copy(colegio = null)) }
    }

    fun onNivelChange(valor: String) {
        _estado.update { it.copy(nivel = valor, errores = it.errores.copy(nivel = null)) }
    }

    fun onCarreraChange(valor: String) {
        _estado.update { it.copy(carrera = valor, errores = it.errores.copy(carrera = null)) }
    }

    fun onModalidadChange(valor: String) {
        _estado.update { it.copy(modalidad = valor, errores = it.errores.copy(modalidad = null)) }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update { it.copy(observaciones = valor) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(aceptaTerminos = null)) }
    }

    fun validar(): Boolean {
        val actual = estado.value
        val errores = RegistroErrores(
            nombre = if (actual.nombre.isBlank()) "El nombre es obligatorio" else null,
            rut = when {
                actual.rut.isBlank() -> "El RUT es obligatorio"
                !Validadores.rutValido(actual.rut) -> MSG_RUT_INVALIDO
                else -> null
            },
            correo = when {
                actual.correo.isBlank() -> "El correo es obligatorio"
                !Validadores.correoValido(actual.correo) -> MSG_CORREO_INVALIDO
                else -> null
            },
            telefono = when {
                actual.telefono.isBlank() -> "El teléfono es obligatorio"
                !Validadores.telefonoValido(actual.telefono) -> "Use formato 9XXXXXXXX"
                else -> null
            },
            colegio = if (actual.colegio.isBlank()) "El colegio es obligatorio" else null,
            nivel = if (actual.nivel.isBlank()) "Selecciona un curso" else null,
            carrera = if (actual.carrera.isBlank()) "Selecciona una carrera" else null,
            // La modalidad ya no está en el formulario del boceto, por eso no se valida
            aceptaTerminos = if (!actual.aceptaTerminos) "Debes marcar la autorización del alumno" else null
        )

        _estado.update { it.copy(errores = errores) }
        return errores.nombre == null && errores.rut == null && errores.correo == null &&
                errores.telefono == null && errores.colegio == null && errores.nivel == null &&
                errores.carrera == null && errores.aceptaTerminos == null
    }

    fun guardar() {
        if (!validar()) return

        val f = estado.value
        viewModelScope.launch {
            val prospecto = Prospecto(
                eventoId = f.eventoId.trim(),
                nombreCompleto = f.nombre.trim(),
                rut = Validadores.normalizarRut(f.rut),
                correoCifrado = CryptoManager.cifrar(f.correo.trim()),
                telefonoCifrado = CryptoManager.cifrar(f.telefono.replace(" ", "")),
                colegio = f.colegio.trim(),
                nivelEducativo = f.nivel,
                carrera = f.carrera,
                modalidad = f.modalidad,
                observaciones = f.observaciones.trim(),
                fechaCaptura = System.currentTimeMillis()
            )
            if (repo.guardar(prospecto)) {
                // Limpia el formulario pero mantiene evento y colegio para el siguiente prospecto
                _estado.value = RegistroUiState(eventoId = f.eventoId, colegio = f.colegio)
                _guardados.emit(
                    ResumenProspecto(
                        nombre = f.nombre.trim(),
                        curso = f.nivel,
                        carrera = f.carrera,
                        eventoId = f.eventoId.trim(),
                        liceo = f.colegio.trim()
                    )
                )
            } else {
                _mensajes.emit("Ese RUT ya fue registrado en este evento")
            }
        }
    }
}
