package cl.therionlabs.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import cl.therionlabs.myapplication.data.RetrofitClient
import cl.therionlabs.myapplication.model.EventoQr
import cl.therionlabs.myapplication.model.EventosDemo
import cl.therionlabs.myapplication.model.ResultadoEvento
import cl.therionlabs.myapplication.model.ResumenProspecto
import cl.therionlabs.myapplication.model.aEventoQr
import cl.therionlabs.myapplication.navigation.NavigationEvent
import cl.therionlabs.myapplication.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()
    private val _evento = MutableStateFlow<EventoQr?>(null)
    val evento: StateFlow<EventoQr?> = _evento.asStateFlow()

    fun seleccionarEvento(evento: EventoQr) {
        _evento.value = evento
    }
    private val _ultimoGuardado = MutableStateFlow<ResumenProspecto?>(null)
    val ultimoGuardado: StateFlow<ResumenProspecto?> = _ultimoGuardado.asStateFlow()

    fun registrarGuardado(resumen: ResumenProspecto) {
        _ultimoGuardado.value = resumen
    }

    suspend fun resolverEvento(codigo: String): ResultadoEvento {
        val id = codigo.trim().uppercase()
        val sinRed = ResultadoEvento.SinConexion(EventosDemo.desdeCodigo(id))
        return try {
            val respuesta = RetrofitClient.api.obtenerEvento(id)
            val dto = respuesta.body()
            when {
                respuesta.code() == 404 -> ResultadoEvento.NoExiste
                !respuesta.isSuccessful || dto == null -> sinRed
                dto.estado == "CERRADO" -> ResultadoEvento.Cerrado
                else -> ResultadoEvento.Encontrado(dto.aEventoQr())
            }
        } catch (e: IOException) {
            sinRed
        }
    }

    fun popTo(screen: Screen) {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.PopTo(screen))
        }
    }

    fun navigateTo(screen: Screen) {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(screen))
        }
    }

    fun popBackStack(){
        CoroutineScope(Dispatchers.Main).launch{
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }
}