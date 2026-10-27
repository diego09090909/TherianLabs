package cl.therionlabs.myapplication.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Scan : BottomNavItem("dash_scan", "Escanear QR", Icons.Default.QrCodeScanner)
    data object History : BottomNavItem("dash_history", "Eventos", Icons.Default.DateRange)
    data object Profile : BottomNavItem("dash_profile", "Mi Perfil", Icons.Default.Person)
}