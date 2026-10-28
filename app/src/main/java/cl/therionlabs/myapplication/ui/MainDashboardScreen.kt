package cl.therionlabs.myapplication.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.therionlabs.myapplication.navigation.BottomNavItem
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.DashboardViewModel
import cl.therionlabs.myapplication.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth

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

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.History,
        BottomNavItem.Prospectos,
        BottomNavItem.Profile
    )
    val orden = items.map { it.route }

    BackHandler(enabled = menuAbierto) { menuAbierto = false }

    val acciones = listOf(
        AccionMenu("Ingresar código a mano", Icons.Default.Keyboard, false) {
            menuAbierto = false
            mainViewModel.navigateTo(Screen.CodigoManual)
        },
        AccionMenu("Escanear QR del evento", Icons.Default.QrCodeScanner, true) {
            menuAbierto = false
            mainViewModel.navigateTo(Screen.Scan)
        }
    )

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
                        BottomNavItem.Home.route -> InicioTab(
                            estado = estado,
                            nombre = nombre,
                            onVerProspectos = { pestana = BottomNavItem.Prospectos.route }
                        )
                        BottomNavItem.History.route -> EventosTab(
                            estado,
                            onEscanear = { mainViewModel.navigateTo(Screen.Scan) }
                        )
                        BottomNavItem.Prospectos.route -> ProspectosTab()
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
                items.forEach { item ->
                    NavigationBarItem(
                        selected = pestana == item.route,
                        onClick = { pestana = item.route; menuAbierto = false },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.route == BottomNavItem.Prospectos.route && estado.porEnviar > 0) {
                                        Badge(containerColor = DuocYellow, contentColor = DuocNavy) {
                                            Text(estado.porEnviar.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DuocAzulAcento,
                            selectedTextColor = DuocNavy,
                            indicatorColor = DuocAzulSuave,
                            unselectedIconColor = DuocGris,
                            unselectedTextColor = DuocGris
                        )
                    )
                }
            }
        }

        MenuFlotante(
            abierto = menuAbierto,
            onToggle = { menuAbierto = !menuAbierto },
            acciones = acciones
        )
    }
}

// Una opción del botón flotante. La primera de la lista queda arriba; la última, junto al botón.
internal data class AccionMenu(
    val texto: String,
    val icono: ImageVector,
    val destacada: Boolean,
    val onClick: () -> Unit
)

// Botón flotante "+" que despliega opciones. Se usa en el panel y en "Prospecto guardado".
@Composable
internal fun MenuFlotante(
    abierto: Boolean,
    onToggle: () -> Unit,
    acciones: List<AccionMenu>,
    bottomPadding: Dp = 96.dp
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
                .padding(end = 16.dp, bottom = bottomPadding),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            acciones.forEach { accion ->
                AnimatedVisibility(
                    visible = abierto,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    OpcionMenu(accion.texto, accion.icono, accion.destacada, accion.onClick)
                }
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