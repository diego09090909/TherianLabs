package cl.therionlabs.myapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.therionlabs.myapplication.CapturaApp
import cl.therionlabs.myapplication.data.AuthRepository
import cl.therionlabs.myapplication.data.Estado
import cl.therionlabs.myapplication.data.Prospecto
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class EventoResumen(
    val eventoId: String,
    val colegio: String,
    val ultimaFecha: Long,
    val total: Int,
    val pendientes: Int
)

data class RegistroReciente(
    val nombre: String,
    val colegio: String,
    val fecha: Long,
    val pendiente: Boolean
)

data class DashboardUiState(
    val hoy: Int = 0,
    val porEnviar: Int = 0,
    val total: Int = 0,
    val eventos: List<EventoResumen> = emptyList(),
    val recientes: List<RegistroReciente> = emptyList()
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as CapturaApp).repository
    private val auth = AuthRepository()

    val estado: StateFlow<DashboardUiState> = repo.observarTodos()
        .map { construir(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    // ----- Datos del captador -----
    private val _nombre = MutableStateFlow(calcularNombre())
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    val correo: String? get() = auth.usuarioActual?.email
    val hayCuenta: Boolean get() = auth.usuarioActual != null

    private fun calcularNombre(): String {
        val usuario = auth.usuarioActual
        val guardado = usuario?.displayName?.trim()
        if (!guardado.isNullOrBlank()) return guardado

        val deCorreo = usuario?.email?.substringBefore("@")
            ?.replace('.', ' ')?.replace('_', ' ')?.trim()
        if (deCorreo.isNullOrBlank()) return "captador"
        return deCorreo.split(" ").joinToString(" ") { p -> p.replaceFirstChar { it.uppercase() } }
    }

    fun guardarNombre(nuevo: String, alTerminar: (String?) -> Unit) {
        val limpio = nuevo.trim()
        when {
            !hayCuenta -> alTerminar("En modo prueba no hay una cuenta real para editar.")
            limpio.length < 2 -> alTerminar("Escribe tu nombre (mínimo 2 letras).")
            else -> viewModelScope.launch {
                auth.actualizarNombre(limpio).fold(
                    onSuccess = {
                        _nombre.value = calcularNombre()
                        alTerminar(null)
                    },
                    onFailure = { alTerminar("No se pudo guardar el nombre. Intenta más tarde.") }
                )
            }
        }
    }

    fun cambiarClave(actual: String, nueva: String, alTerminar: (String?) -> Unit) {
        if (!hayCuenta) {
            alTerminar("En modo prueba no hay una cuenta real para editar.")
            return
        }
        viewModelScope.launch {
            auth.cambiarClave(actual, nueva).fold(
                onSuccess = { alTerminar(null) },
                onFailure = { alTerminar(mensajeClave(it)) }
            )
        }
    }

    private fun mensajeClave(e: Throwable): String = when (e) {
        is FirebaseAuthInvalidCredentialsException -> "La contraseña actual no es correcta."
        is FirebaseAuthWeakPasswordException -> "La nueva contraseña es muy débil."
        is FirebaseTooManyRequestsException -> "Demasiados intentos. Espera unos minutos."
        is FirebaseNetworkException -> "Sin conexión a internet. Intenta de nuevo."
        else -> "No se pudo cambiar la contraseña. Intenta más tarde."
    }

    // ----- Resumen de prospectos (Room) -----
    private fun construir(lista: List<Prospecto>): DashboardUiState {
        val inicioDia = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val eventos = lista.groupBy { it.eventoId }
            .map { (id, ps) ->
                EventoResumen(
                    eventoId = id,
                    colegio = ps.first().colegio,
                    ultimaFecha = ps.maxOf { it.fechaCaptura },
                    total = ps.size,
                    pendientes = ps.count { it.estado == Estado.PENDIENTE }
                )
            }
            .sortedByDescending { it.ultimaFecha }

        return DashboardUiState(
            hoy = lista.count { it.fechaCaptura >= inicioDia },
            porEnviar = lista.count { it.estado == Estado.PENDIENTE },
            total = lista.size,
            eventos = eventos,
            recientes = lista.take(3).map {
                RegistroReciente(it.nombreCompleto, it.colegio, it.fechaCaptura, it.estado == Estado.PENDIENTE)
            }
        )
    }
}