package cl.therionlabs.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.therionlabs.myapplication.viewmodels.DashboardUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.style.TextOverflow

internal fun horaOFecha(ms: Long): String {
    val ahora = Calendar.getInstance()
    val d = Calendar.getInstance().apply { timeInMillis = ms }
    val mismoDia = ahora.get(Calendar.YEAR) == d.get(Calendar.YEAR) &&
            ahora.get(Calendar.DAY_OF_YEAR) == d.get(Calendar.DAY_OF_YEAR)
    return if (mismoDia) {
        SimpleDateFormat("HH:mm", Locale.US).format(Date(ms))
    } else {
        SimpleDateFormat("d MMM", Locale.forLanguageTag("es-CL")).format(Date(ms))
    }
}

internal fun fechaCorta(ms: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-CL")).format(Date(ms))

@Composable
fun InicioTab(estado: DashboardUiState, nombre: String) {
    var guiaAbierta by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DuocNavy)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 28.dp)
        ) {
            Text(
                "Hola, $nombre",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Listo para captar prospectos",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaDato(estado.hoy.toString(), "Hoy", Modifier.weight(1f))
                TarjetaDato(estado.porEnviar.toString(), "Por enviar", Modifier.weight(1f))
                TarjetaDato(estado.eventos.size.toString(), "Eventos", Modifier.weight(1f))
            }

            TarjetaGuia(abierta = guiaAbierta, onToggle = { guiaAbierta = !guiaAbierta })

            Text(
                "Últimos registros",
                color = DuocNavy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (estado.recientes.isEmpty()) {
                Text(
                    "Todavía no registras prospectos. Toca el botón + para comenzar.",
                    color = DuocGris,
                    fontSize = 14.sp
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    estado.recientes.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DuocAzulSuave),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    r.nombre.take(1).uppercase(),
                                    color = DuocNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(r.nombre, color = DuocNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${r.colegio} · ${horaOFecha(r.fecha)}", color = DuocGris, fontSize = 12.sp)
                            }
                            if (r.pendiente) {
                                DuocPill("Pendiente", DuocAmarilloPill, DuocAmarilloTexto)
                            } else {
                                DuocPill("Enviado", DuocVerdeSuave, DuocVerde)
                            }
                        }
                    }
                }
            }

            // Espacio para que el botón + no tape el último registro
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun TarjetaDato(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, color = DuocNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(etiqueta, color = DuocGris, fontSize = 12.sp)
    }
}

@Composable
private fun TarjetaGuia(abierta: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onToggle() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DuocAmarilloSuave),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = DuocNavy)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("¿Cómo usar la app?", color = DuocNavy, fontWeight = FontWeight.Bold)
                Text("3 pasos, menos de un minuto", color = DuocGris, fontSize = 12.sp)
            }
            Icon(
                imageVector = if (abierta) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (abierta) "Ocultar guía" else "Mostrar guía",
                tint = DuocGris
            )
        }
        AnimatedVisibility(visible = abierta) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PasoGuia(1, "Escanea el QR del evento", "Toca el botón + y apunta al código del liceo.")
                PasoGuia(2, "Registra al alumno", "Completa el cupón. Funciona sin internet.")
                PasoGuia(3, "Sincroniza", "Los datos suben solos cuando hay conexión.")
            }
        }
    }
}

@Composable
private fun PasoGuia(numero: Int, titulo: String, detalle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(DuocNavy),
            contentAlignment = Alignment.Center
        ) {
            Text(numero.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(titulo, color = DuocNavy, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(detalle, color = DuocGris, fontSize = 12.sp)
        }
    }
}