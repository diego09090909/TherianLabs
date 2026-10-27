package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.therionlabs.myapplication.navigation.BottomNavItem
import cl.therionlabs.myapplication.viewmodels.MainViewModel

@Composable
fun MainDashboardScreen(
    mainViewModel: MainViewModel,
    onStartEventCapture: (String) -> Unit
) {
    val bottomNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Scan,
        BottomNavItem.History,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Scan.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 👈 PESTAÑA 1: Llama directamente a tu ScanQrScreen original con la cámara activa
            composable(BottomNavItem.Scan.route) {
                ScanQrScreen(mainViewModel = mainViewModel)
            }

            // PESTAÑA 2: Historial de Eventos (Lista, Buscador y Botón Sincronizar)
            composable(BottomNavItem.History.route) {
                EventHistoryScreen(mainViewModel = mainViewModel)
            }

            // PESTAÑA 3: Perfil del Captador
            composable(BottomNavItem.Profile.route) {
                CaptadorProfileScreen()
            }
        }
    }
}