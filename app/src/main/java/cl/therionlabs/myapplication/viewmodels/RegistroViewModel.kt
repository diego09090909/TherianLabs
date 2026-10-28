package cl.therionlabs.myapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.CapturaApp
import cl.therionlabs.myapplication.data.AppDatabase
import cl.therionlabs.myapplication.data.CarreraEntity
import cl.therionlabs.myapplication.data.CryptoManager
import cl.therionlabs.myapplication.data.Prospecto
import cl.therionlabs.myapplication.model.Catalogos
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
private const val MSG_FECHA_INVALIDA = "Fecha no válida. Usa el formato dd-mm-aaaa."
private const val MSG_CORREO_INVALIDO = "Ingresa un correo válido, por ejemplo nombre@correo.cl."

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as CapturaApp).repository

    // Canal privado de actualización
    private val _estado = MutableStateFlow(RegistroUiState())

    // Canal público de solo lectura para la vista
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    // Mensajes de una sola vez para la vista (Snackbar)
    private val _mensajes = MutableSharedFlow<String>()
    val mensajes: SharedFlow<String> = _mensajes.asSharedFlow()

    // Se emite cuando un alumno se guardó bien: la vista abre la pantalla de confirmación
    private val _guardados = MutableSharedFlow<ResumenProspecto>()
    val guardados: SharedFlow<ResumenProspecto> = _guardados.asSharedFlow()

    // Lista local para la pantalla de registrados (RF-04: la vista solo muestra nombre y RUT)
    val prospectos = repo.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendientes = repo.contarPendientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Carreras que ya están en la base de datos (tabla "carreras"): alimentan los 3 buscadores
    val carreras: StateFlow<List<CarreraEntity>> =
        AppDatabase.obtenerInstancia(application, viewModelScope)
            .carreraDao()
            .getTodasLasCarreras()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sincronización manual (botón de la pantalla de prospectos)
    private val _sincronizando = MutableStateFlow(false)
    val sincronizando: StateFlow<Boolean> = _sincronizando.asStateFlow()

    fun sincronizarAhora() {
        if (_sincronizando.value) return
        viewModelScope.launch {
            if (pendientes.value == 0) {
                _mensajes.emit("No hay registros pendientes por sincronizar.")
                return@launch
            }
            _sincronizando.value = true
            // Sin red o con el servidor caído devuelve false y los datos quedan PENDIENTE
            val completo = try {
                repo.sincronizarPendientes()
            } catch (e: Exception) {
                false
            }
            _sincronizando.value = false
            _mensajes.emit(
                if (completo) {
                    "Datos sincronizados con el servidor."
                } else {
                    "No se pudo sincronizar. Los datos siguen guardados en el teléfono y se reintentará solo."
                }
            )
        }
    }

    fun onEventoChange(valor: String) {
        _estado.update { it.copy(eventoId = valor) }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onApellidosChange(valor: String) {
        _estado.update { it.copy(apellidos = valor, errores = it.errores.copy(apellidos = null)) }
    }

    fun onSexoChange(valor: String) {
        _estado.update { it.copy(sexo = valor) }
    }

    // RF-03: el RUT se autoformatea y se valida en tiempo real mientras se escribe
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

    // Se escriben solo números: el campo pone los guiones (dd-mm-aaaa)
    fun onFechaNacimientoChange(valor: String) {
        val formateada = Validadores.formatearFecha(valor)
        _estado.update {
            it.copy(
                fechaNacimiento = formateada,
                errores = it.errores.copy(
                    fechaNacimiento = if (formateada.length == 10 && !Validadores.fechaNacimientoValida(formateada)) {
                        MSG_FECHA_INVALIDA
                    } else null
                )
            )
        }
    }

    fun onComunaResidenciaChange(valor: String) {
        _estado.update { it.copy(comunaResidencia = valor, errores = it.errores.copy(comunaResidencia = null)) }
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

    fun onActividadChange(valor: String) {
        _estado.update {
            it.copy(
                actividad = valor,
                // Si cambia a una opción distinta de "Otra", se descarta el texto
                actividadOtra = if (valor == Catalogos.ACTIVIDAD_OTRA) it.actividadOtra else "",
                errores = it.errores.copy(actividad = null, actividadOtra = null)
            )
        }
    }

    fun onActividadOtraChange(valor: String) {
        _estado.update { it.copy(actividadOtra = valor, errores = it.errores.copy(actividadOtra = null)) }
    }

    fun onColegioChange(valor: String) {
        _estado.update { it.copy(colegio = valor, errores = it.errores.copy(colegio = null)) }
    }

    fun onComunaColegioChange(valor: String) {
        _estado.update { it.copy(comunaColegio = valor) }
    }

    fun onJornadaChange(valor: String) {
        _estado.update { it.copy(jornada = valor, errores = it.errores.copy(jornada = null)) }
    }

    fun onSedeToggle(sede: String) {
        _estado.update {
            val nuevas = if (sede in it.sedes) it.sedes - sede else it.sedes + sede
            it.copy(sedes = nuevas, errores = it.errores.copy(sedes = null))
        }
    }

    fun onNivelInteresChange(valor: String) {
        _estado.update { it.copy(nivelInteres = valor) }
    }

    fun onAfirmacionChange(valor: String) {
        _estado.update { it.copy(afirmacion = valor) }
    }

    // posicion: 1, 2 o 3 (prioridad). valor vacío = quitar la elección.
    fun onCarreraChange(posicion: Int, valor: String) {
        _estado.update {
            when (posicion) {
                1 -> it.copy(carrera1 = valor, errores = it.errores.copy(carrera1 = null))
                2 -> it.copy(carrera2 = valor, errores = it.errores.copy(carrera2 = null))
                else -> it.copy(carrera3 = valor, errores = it.errores.copy(carrera3 = null))
            }
        }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(aceptaTerminos = null)) }
    }

    fun validar(): Boolean {
        val a = estado.value
        val errores = RegistroErrores(
            rut = when {
                a.rut.isBlank() -> "El RUT es obligatorio"
                !Validadores.rutValido(a.rut) -> MSG_RUT_INVALIDO
                else -> null
            },
            nombre = if (a.nombre.isBlank()) "El nombre es obligatorio" else null,
            apellidos = if (a.apellidos.isBlank()) "Los apellidos son obligatorios" else null,
            fechaNacimiento = when {
                a.fechaNacimiento.isBlank() -> "La fecha de nacimiento es obligatoria"
                !Validadores.fechaNacimientoValida(a.fechaNacimiento) -> MSG_FECHA_INVALIDA
                else -> null
            },
            comunaResidencia = if (a.comunaResidencia.isBlank()) "Indica la comuna donde vive" else null,
            telefono = when {
                a.telefono.isBlank() -> "El celular es obligatorio"
                !Validadores.telefonoValido(a.telefono) -> "Use formato 9XXXXXXXX"
                else -> null
            },
            correo = when {
                a.correo.isBlank() -> "El correo es obligatorio"
                !Validadores.correoValido(a.correo) -> MSG_CORREO_INVALIDO
                else -> null
            },
            actividad = if (a.actividad.isBlank()) "Selecciona la actividad actual" else null,
            actividadOtra = if (a.actividad == Catalogos.ACTIVIDAD_OTRA && a.actividadOtra.isBlank()) {
                "Indica cuál es su actividad"
            } else null,
            colegio = if (a.colegio.isBlank()) "El establecimiento es obligatorio" else null,
            jornada = if (a.jornada.isBlank()) "Selecciona una jornada" else null,
            sedes = if (a.sedes.isEmpty()) "Marca al menos una sede de interés" else null,
            carrera1 = if (a.carrera1.isBlank()) "Elige tu 1ª opción de carrera" else null,
            carrera2 = if (a.carrera2.isBlank()) "Elige tu 2ª opción de carrera" else null,
            carrera3 = if (a.carrera3.isBlank()) "Elige tu 3ª opción de carrera" else null,
            aceptaTerminos = if (!a.aceptaTerminos) "El alumno debe marcar \"Sí, autorizo\" para poder guardar" else null
        )

        _estado.update { it.copy(errores = errores) }
        return errores.cantidad() == 0
    }

    // Guarda SIEMPRE en Room primero. La subida a la API la hace WorkManager cuando hay red.
    fun guardar() {
        if (!validar()) return

        val f = estado.value
        val nivel = if (f.actividad == Catalogos.ACTIVIDAD_OTRA) {
            "Otra: ${f.actividadOtra.trim()}"
        } else f.actividad
        val nombreCompleto = "${f.nombre.trim()} ${f.apellidos.trim()}"

        viewModelScope.launch {
            val prospecto = Prospecto(
                eventoId = f.eventoId.trim(),
                nombreCompleto = nombreCompleto,
                rut = Validadores.normalizarRut(f.rut),
                correoCifrado = CryptoManager.cifrar(f.correo.trim()),
                telefonoCifrado = CryptoManager.cifrar(f.telefono.replace(" ", "")),
                colegio = f.colegio.trim(),
                nivelEducativo = nivel,
                carrera = f.carrera1,
                modalidad = "",
                observaciones = "",
                fechaCaptura = System.currentTimeMillis(),
                carrera2 = f.carrera2,
                carrera3 = f.carrera3,
                sexo = f.sexo,
                fechaNacimiento = f.fechaNacimiento,
                comunaResidencia = f.comunaResidencia.trim(),
                comunaColegio = f.comunaColegio.trim(),
                jornada = f.jornada,
                // Mismo orden que en el cupón, no el orden en que se tocaron
                sedes = Catalogos.sedesPorRegion.flatMap { r -> r.sedes.map { s -> s.nombre } }
                    .filter { it in f.sedes }
                    .joinToString("|"),
                nivelInteres = f.nivelInteres,
                afirmacion = f.afirmacion,
                autorizaDatos = f.aceptaTerminos
            )
            if (repo.guardar(prospecto)) {
                // Limpia el formulario pero mantiene evento y colegio para el siguiente prospecto
                _estado.value = RegistroUiState(
                    eventoId = f.eventoId,
                    colegio = f.colegio,
                    comunaColegio = f.comunaColegio
                )
                _guardados.emit(
                    ResumenProspecto(
                        nombre = nombreCompleto,
                        curso = nivel,
                        carrera = f.carrera1,
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