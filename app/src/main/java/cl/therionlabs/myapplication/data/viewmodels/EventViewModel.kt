package cl.therionlabs.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import cl.therionlabs.myapplication.model.Evento

sealed interface EventUiState {
    data object Idle : EventUiState
    data object Loading : EventUiState
    data class Success(val evento: Evento) : EventUiState
    data class Error(val message: String) : EventUiState
}

class EventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<EventUiState>(EventUiState.Idle)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    fun buscarEventoPorCodigo(codigo: String) {
        _uiState.value = EventUiState.Loading


        if (codigo.trim().uppercase() == "EVT-0042") {
            _uiState.value = EventUiState.Success(
                Evento(
                    id = "EVT-0042",
                    nombreLiceo = "Liceo Bicentenario Ejemplo",
                    nombreEvento = "Feria Vocacional 2026",
                    fecha = "14 oct 2026"
                )
            )
        } else {
            _uiState.value = EventUiState.Error("Código de evento no encontrado")
        }
    }
}