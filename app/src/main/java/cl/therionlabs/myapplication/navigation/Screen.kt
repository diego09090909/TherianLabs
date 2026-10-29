package cl.therionlabs.myapplication.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_page")
    data object Register : Screen("register_page")
    data object Lista : Screen("list_page")

    data object Login: Screen("login")

    data object RegistroCaptador : Screen("registro_captador")
    data object Scan : Screen("scan_page")

    data object CodigoManual : Screen("codigo_manual_page")
    data object EventoDetectado : Screen("evento_detectado_page")
    data object Guardado : Screen("guardado_page")

    data object Dashboard : Screen("main_dashboard")
}
