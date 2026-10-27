package cl.therionlabs.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.therionlabs.myapplication.navigation.NavigationEvent
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.ui.DuocTopBar
import cl.therionlabs.myapplication.ui.EventoDetectadoScreen
import cl.therionlabs.myapplication.ui.GuardadoScreen
import cl.therionlabs.myapplication.ui.HomeScreen
import cl.therionlabs.myapplication.ui.ListScreen
import cl.therionlabs.myapplication.ui.LoginScreen
import cl.therionlabs.myapplication.ui.MainDashboardScreen
import cl.therionlabs.myapplication.ui.RegisterScreen
import cl.therionlabs.myapplication.ui.RegistroCaptadorScreen
import cl.therionlabs.myapplication.ui.theme.MyApplicationTheme
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme(darkTheme = false) {
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                // Colector de eventos de navegación emitidos desde el ViewModel
                LaunchedEffect(Unit) {
                    viewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo ->
                                navController.navigate(event.screen.route)

                            is NavigationEvent.PopTo -> {
                                if (!navController.popBackStack(event.screen.route, false)) {
                                    navController.navigate(event.screen.route)
                                }
                            }

                            NavigationEvent.PopBackStack -> {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route // La app arranca en la pantalla de bienvenida / Home
                ) {

                    // --- PANTALLA PRINCIPAL (HOME) ---
                    composable(Screen.Home.route) {
                        HomeScreen(viewModel = viewModel)
                    }

                    // --- PANTALLA DE LOGIN (FIREBASE AUTH) ---
                    composable(Screen.Login.route) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            DuocTopBar("Iniciar sesión") { viewModel.popBackStack() }
                            Box(modifier = Modifier.weight(1f)) {
                                LoginScreen(
                                    onLoginSuccess = {
                                        // Login exitoso: Navega al Dashboard (Menú Principal) limpiando el Login de la pila
                                        navController.navigate("main_dashboard") {
                                            popUpTo(Screen.Home.route) { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate("registro_captador")
                                    }
                                )
                            }
                        }
                    }

                    // --- REGISTRO DE CAPTADOR ---
                    composable("registro_captador") {
                        Column(modifier = Modifier.fillMaxSize()) {
                            DuocTopBar("Registro de captador") { viewModel.popBackStack() }
                            Box(modifier = Modifier.weight(1f)) {
                                RegistroCaptadorScreen(
                                    onVolverLogin = {
                                        navController.popBackStack() // Al crear cuenta vuelve al Login/Home
                                    }
                                )
                            }
                        }
                    }

                    // --- DASHBOARD INTERMEDIO CON BOTTOM NAVIGATION BAR ---
                    composable("main_dashboard") {
                        MainDashboardScreen(
                            mainViewModel = viewModel,
                            onStartEventCapture = { eventId ->
                                // Al confirmar el QR o evento, inicia la toma de prospectos
                                navController.navigate(Screen.Guardado.route)
                            }
                        )
                    }

                    // --- FLUJO DE CAPTURA Y EVENTO ---
                    composable(Screen.EventoDetectado.route) {
                        EventoDetectadoScreen(mainViewModel = viewModel)
                    }

                    composable(Screen.Guardado.route) {
                        GuardadoScreen(mainViewModel = viewModel)
                    }

                    composable(Screen.Lista.route) {
                        ListScreen(mainViewModel = viewModel)
                    }
                }
            }
        }
    }
}