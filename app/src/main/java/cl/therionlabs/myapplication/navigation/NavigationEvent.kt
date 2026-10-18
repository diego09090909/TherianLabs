package cl.therionlabs.myapplication.navigation

sealed class NavigationEvent {
    data class NavigateTo(val screen: Screen) : NavigationEvent()
    data object PopBackStack : NavigationEvent()
    data class PopTo(val screen: Screen) : NavigationEvent()
}