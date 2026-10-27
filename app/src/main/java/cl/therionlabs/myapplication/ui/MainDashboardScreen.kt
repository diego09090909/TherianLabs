package cl.therionlabs.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.navigation.BottomNavItem
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.DashboardViewModel
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

@Composable
fun MainDashboardScreen(
    mainViewModel: MainViewModel,
    onStartEventCapture: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    var pestana by rememberSaveable { mutableStateOf(BottomNavItem.Home.route) }
    var menuAbierto by remember { mutableStateOf(false) }
    val estado by dashboardViewModel.estado.collectAsState()
    val nombre by dashboardViewModel.nombre.collectAsState()

    val orden = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.History.route,
        BottomNavItem.Profile.route
    )

    BackHandler(enabled = menuAbierto) { menuAbierto = false }

    fun irAEscanear() {
        menuAbierto = false
        mainViewModel.navigateTo(Screen.Scan)
    }

    Box(modifier = Modifier.fillMaxSize().background(DuocFondo)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = pestana,
                    transitionSpec = {
                        val dir = if (orden.indexOf(targetState) > orden.indexOf(initialState)) 1 else -1
                        (slideInHorizontally(tween(300)) { dir * it / 6 } + fadeIn(tween(300))) togetherWith
                                (slideOutHorizontally(tween(300)) { -dir * it / 6 } + fadeOut(tween(200)))
                    },
                    label = "pestanas"
                ) { destino ->
                    when (destino) {
                        BottomNavItem.Home.route -> InicioTab(estado, nombre)
                        BottomNavItem.History.route -> EventosTab(estado, onEscanear = { irAEscanear() })
                        else -> PerfilTab(
                            estado = estado,
                            nombre = nombre,
                            correo = dashboardViewModel.correo,
                            onGuardarNombre = { nuevo, cb -> dashboardViewModel.guardarNombre(nuevo, cb) },
                            onCambiarClave = { act, nue, cb -> dashboardViewModel.cambiarClave(act, nue, cb) },
                            onCerrarSesion = {
                                FirebaseAuth.getInstance().signOut()
                                onLogout()
                            }
                        )
                    }
                }
            }

            NavigationBar(containerColor = Color.White) {
                orden.forEach { ruta ->
                    val item = when (ruta) {
                        BottomNavItem.Home.route -> BottomNavItem.Home
                        BottomNavItem.History.route -> BottomNavItem.History
                        else -> BottomNavItem.Profile
                    }
                    NavigationBarItem(
                        selected = pestana == item.route,
                        onClick = { pestana = item.route; menuAbierto = false },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DuocNavy,
                            selectedTextColor = DuocNavy,
                            indicatorColor = DuocAmarilloPill,
                            unselectedIconColor = DuocGris,
                            unselectedTextColor = DuocGris
                        )
                    )
                }
            }
        }

        MenuEscaneo(
            abierto = menuAbierto,
            onToggle = { menuAbierto = !menuAbierto },
            onEscanear = { irAEscanear() },
            onManual = { irAEscanear() }
        )
    }
}

@Composable
private fun MenuEscaneo(
    abierto: Boolean,
    onToggle: () -> Unit,
    onEscanear: () -> Unit,
    onManual: () -> Unit
) {
    val giro by animateFloatAsState(if (abierto) 45f else 0f, label = "giroMas")

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = abierto, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle
                    )
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 96.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AnimatedVisibility(
                visible = abierto,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                OpcionMenu("Ingresar código a mano", Icons.Default.Keyboard, destacada = false, onClick = onManual)
            }
            AnimatedVisibility(
                visible = abierto,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                OpcionMenu("Escanear QR del evento", Icons.Default.QrCodeScanner, destacada = true, onClick = onEscanear)
            }
            FloatingActionButton(
                onClick = onToggle,
                containerColor = DuocYellow,
                contentColor = DuocNavy,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = if (abierto) "Cerrar menú" else "Abrir menú",
                    modifier = Modifier.rotate(giro)
                )
            }
        }
    }
}

@Composable
private fun OpcionMenu(texto: String, icono: ImageVector, destacada: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(texto, color = DuocNavy, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (destacada) DuocYellow else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = texto, tint = DuocNavy)
        }
    }
}