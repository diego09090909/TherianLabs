package cl.therionlabs.myapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Group

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem("dash_home", "Inicio", Icons.Default.Home)
    data object Scan : BottomNavItem("dash_scan", "Escanear QR", Icons.Default.QrCodeScanner)
    data object History : BottomNavItem("dash_history", "Eventos", Icons.Default.DateRange)
    data object Prospectos : BottomNavItem("dash_prospectos", "Prospectos", Icons.Default.Group)
    data object Profile : BottomNavItem("dash_profile", "Mi Perfil", Icons.Default.Person)
}