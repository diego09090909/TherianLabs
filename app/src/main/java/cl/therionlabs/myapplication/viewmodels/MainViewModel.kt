package cl.therionlabs.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import cl.therionlabs.myapplication.navigation.NavigationEvent
import cl.therionlabs.myapplication.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

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