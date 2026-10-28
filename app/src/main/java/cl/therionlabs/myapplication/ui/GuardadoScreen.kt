package cl.therionlabs.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.navigation.Screen
import cl.therionlabs.myapplication.viewmodels.MainViewModel

@Composable
fun GuardadoScreen(mainViewModel: MainViewModel) {
    val resumen by mainViewModel.ultimoGuardado.collectAsState()
    val r = resumen

    LaunchedEffect(r) {
        if (r == null) mainViewModel.popBackStack()
    }

    if (r != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DuocFondo)
                .systemBarsPadding()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                // Check de doble aro
                Box(
                    Modifier.size(104.dp).clip(CircleShape).background(DuocVerdeSuave),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier.size(72.dp).clip(CircleShape).background(DuocTurquesa),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = DuocNavy,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Text(
                    text = "Prospecto guardado en el dispositivo",
                    color = DuocNavy,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Aviso: fondo azul suave, sin borde
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DuocAzulSuave)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Se subirá al servidor automáticamente cuando el teléfono detecte conexión.",
                        color = DuocNavy,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Tarjeta de datos
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tarjeta(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = r.nombre,
                            color = DuocNavy,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${r.curso} · ${r.carrera}",
                            color = DuocGris,
                            fontSize = 14.sp
                        )
                    }
                    HorizontalDivider(color = DuocBorde.copy(alpha = 0.6f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Evento", color = DuocGris, fontSize = 14.sp)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "${r.eventoId} · ${r.liceo}",
                            color = DuocNavy,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = DuocBorde.copy(alpha = 0.6f))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estado", color = DuocGris, fontSize = 14.sp)
                        Spacer(Modifier.width(16.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(DuocAmarilloPill)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = DuocAmarilloTexto,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Pendiente de sincronizar",
                                color = DuocAmarilloTexto,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            // Vuelve al formulario, que ya quedó limpio con el mismo evento
            DuocBotonAmarillo(
                texto = "Registrar otro alumno",
                onClick = { mainViewModel.popBackStack() }
            )
            Spacer(Modifier.height(12.dp))
            // Todavía no existe el panel del boceto (pág. 4): por ahora vuelve al escáner
            DuocBotonContorno(
                texto = "Volver al panel",
                color = DuocAzulAcento,
                onClick = { mainViewModel.popTo(Screen.Scan) }
            )
        }
    }
}