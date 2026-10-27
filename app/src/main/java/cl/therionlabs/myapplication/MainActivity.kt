package cl.therionlabs.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import cl.therionlabs.myapplication.ui.ScanQrScreen
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
                    startDestination = Screen.Home.route,
                    // Transiciones por defecto: la pantalla nueva entra desde la derecha
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            tween(350),
                            initialOffset = { it / 3 }
                        ) + fadeIn(tween(350))
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            tween(350),
                            targetOffset = { it / 6 }
                        ) + fadeOut(tween(250))
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            tween(350),
                            initialOffset = { it / 6 }
                        ) + fadeIn(tween(350))
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            tween(350),
                            targetOffset = { it / 3 }
                        ) + fadeOut(tween(250))
                    }
                ) {

                    // 1. HOME: queda quieta detrás mientras Login/Registro suben encima
                    composable(
                        route = Screen.Home.route,
                        enterTransition = { fadeIn(tween(500)) },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None }
                    ) {
                        HomeScreen(viewModel = viewModel)
                    }

                    // 2. LOGIN: sube desde abajo como una hoja
                    composable(
                        route = Screen.Login.route,
                        enterTransition = {
                            slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 350)
                            )
                        },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = {
                            slideOutVertically(
                                targetOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 350)
                            )
                        }
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            DuocTopBar("Iniciar sesión") { viewModel.popBackStack() }
                            Box(modifier = Modifier.weight(1f)) {
                                LoginScreen(
                                    onLoginSuccess = {
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

                    // 3. REGISTRO DE CAPTADOR: sube desde abajo
                    composable(
                        route = "registro_captador",
                        enterTransition = {
                            slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 350)
                            )
                        },
                        popExitTransition = {
                            slideOutVertically(
                                targetOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 350)
                            )
                        }
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            DuocTopBar("Registro de captador") { viewModel.popBackStack() }
                            Box(modifier = Modifier.weight(1f)) {
                                RegistroCaptadorScreen(
                                    onVolverLogin = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }

                    // 4. MAIN DASHBOARD: al iniciar sesión aparece con fundido + zoom suave
                    composable(
                        route = "main_dashboard",
                        enterTransition = {
                            fadeIn(tween(600)) + scaleIn(initialScale = 0.92f, animationSpec = tween(600))
                        },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None }
                    ) {
                        MainDashboardScreen(
                            mainViewModel = viewModel,
                            onLogout = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 4b. ESCANEO QR: sube desde abajo (se abre desde el botón +)
                    composable(
                        route = Screen.Scan.route,
                        enterTransition = {
                            slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 350)
                            )
                        },
                        popExitTransition = {
                            slideOutVertically(
                                targetOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 350)
                            )
                        }
                    ) {
                        ScanQrScreen(mainViewModel = viewModel)
                    }

                    // 5. RESTO DE PANTALLAS (usan la transición por defecto)
                    composable(Screen.EventoDetectado.route) {
                        EventoDetectadoScreen(mainViewModel = viewModel)
                    }

                    composable(Screen.Register.route) {
                        RegisterScreen(mainViewModel = viewModel)
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