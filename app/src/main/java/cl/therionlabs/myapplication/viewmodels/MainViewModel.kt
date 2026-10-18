package cl.therionlabs.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import cl.therionlabs.myapplication.model.EventoQr
import cl.therionlabs.myapplication.model.ResumenProspecto
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